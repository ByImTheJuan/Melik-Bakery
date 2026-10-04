package com.hyd.pipes_bakery_backend.service;

import java.text.Normalizer;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.CheckoutSnapshot;
import com.hyd.pipes_bakery_backend.exception.CartIsEmptyException;
import com.hyd.pipes_bakery_backend.exception.InvalidAddressException;
import com.hyd.pipes_bakery_backend.exception.InvalidDeliveryDateException;
import com.hyd.pipes_bakery_backend.exception.InvalidOrderStatusTransitionException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.AddressMapper;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.notification.OrderStatusChangedEvent;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class OrderService implements IOrderService {

    private static final String PUBLIC_ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PUBLIC_ID_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    /** The bakery works on Bogota time: "today" for the delivery rules is today in Bogota. */
    public static final ZoneId BAKERY_ZONE = ZoneId.of("America/Bogota");
    /** Orders need this many days of preparation: the earliest delivery is today + 4 days. */
    public static final int MIN_DELIVERY_LEAD_DAYS = 4;
    /** How far ahead a delivery can be booked. */
    public static final int MAX_DELIVERY_LEAD_DAYS = 90;

    private static final Map<OrderStatus, OrderStatus> ADMIN_ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PAID, OrderStatus.PREPARING,
            OrderStatus.PREPARING, OrderStatus.SHIPPED,
            OrderStatus.SHIPPED, OrderStatus.DELIVERED
    );

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartStorage cartStorage;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CartStorage cartStorage,
            OrderMapper orderMapper,
            AddressMapper addressMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this(orderRepository, productRepository, cartStorage, orderMapper, addressMapper, eventPublisher, Clock.system(BAKERY_ZONE));
    }

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CartStorage cartStorage,
            OrderMapper orderMapper,
            AddressMapper addressMapper,
            ApplicationEventPublisher eventPublisher,
            Clock clock
    ) {
        this.clock = clock;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartStorage = cartStorage;
        this.orderMapper = orderMapper;
        this.addressMapper = addressMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        // Most recent first: new orders are the ones the bakery has to act on
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDTO getOrderById(@NonNull String orderId) {
        return orderMapper.toDto(orderRepository.findByPublicId(orderId).orElseThrow(() ->
                new ResourceNotFoundException("Order not found with id " + orderId)
        ));
    }

    @Override
    public OrderResponseDTO cancelOrder(@NonNull String orderId) {
        Order order = findOrderByPublicId(orderId);
        return orderMapper.toDto(applyStatus(order, OrderStatus.CANCELLED));
    }

    @Override
    public OrderResponseDTO updateOrderStatus(@NonNull String orderId, OrderStatus requestedStatus) {
        Order order = findOrderByPublicId(orderId);

        OrderStatus currentStatus = order.getStatus();
        OrderStatus allowedNext = ADMIN_ALLOWED_TRANSITIONS.get(currentStatus);

        if (allowedNext == null || allowedNext != requestedStatus) {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot transition order from " + currentStatus + " to " + requestedStatus
            );
        }

        return orderMapper.toDto(applyStatus(order, requestedStatus));
    }

    private Order findOrderByPublicId(String orderId) {
        return orderRepository.findByPublicId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    private Order applyStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(saved.getPublicId(), status));
        return saved;
    }

    @Override
    public CheckoutSnapshot buildCheckoutSnapshot(UUID cartId, CheckoutOrderRequestDTO request) {
        validateShippingAddress(request.getShippingAddress());
        validateDeliveryDate(request.getDeliveryDate());

        ShoppingCart cart = cartStorage.getCart(cartId);
        if (cart.isEmpty()) {
            throw new CartIsEmptyException("Cart is empty. Impossible to checkout");
        }

        List<CheckoutSnapshot.Item> items = cart.getItems().stream()
                .map(item -> item.isCustomCakeLine()
                        ? CheckoutSnapshot.Item.customCake(item.getProductName(), item.getQuantity(), item.getUnitPriceAtAdd(), item.getCustomCake())
                        : new CheckoutSnapshot.Item(item.getProductId(), item.getQuantity(), item.getUnitPriceAtAdd()))
                .toList();

        return new CheckoutSnapshot(request, items, cart.getShippingCost());
    }

    @Transactional
    @Override
    public Order createPaidOrder(CheckoutSnapshot snapshot) {
        CheckoutOrderRequestDTO request = snapshot.getRequest();

        Order order = new Order(
                request.getClientFirstName(),
                request.getClientLastName(),
                request.getClientEmail(),
                request.getClientPhoneNumber(),
                addressMapper.toSnapshotEntity(request.getShippingAddress()),
                request.getReceiverName(),
                snapshot.getShippingCost()
        );
        order.setPublicId(generateUniquePublicId());
        order.setStatus(OrderStatus.PAID);
        order.setDeliveryDate(request.getDeliveryDate());
        order.setDeliverySlot(request.getDeliverySlot());

        List<OrderItem> items = snapshot.getItems().stream()
                .map(item -> {
                    if (item.isCustomCakeLine()) {
                        return OrderItem.customCake(item.getName(), item.getQuantity(), item.getUnitPrice(), item.getCustomCake());
                    }

                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Product not found with id " + item.getProductId()
                                    )
                            );

                    OrderItem orderItem = new OrderItem(product, item.getQuantity(), item.getUnitPrice());
                    orderItem.setItemName(product.getName());
                    return orderItem;
                })
                .toList();

        order.setItems(items);
        Order saved = orderRepository.save(order);
        // The "order received" email goes out once the payment transaction commits
        eventPublisher.publishEvent(new OrderStatusChangedEvent(saved.getPublicId(), OrderStatus.PAID));
        return saved;
    }

    @Override
    public void validateDeliveryDate(LocalDate deliveryDate) {
        if (deliveryDate == null) {
            throw new InvalidDeliveryDateException("Delivery date is required");
        }

        LocalDate today = LocalDate.now(clock.withZone(BAKERY_ZONE));
        if (deliveryDate.isBefore(today.plusDays(MIN_DELIVERY_LEAD_DAYS))) {
            throw new InvalidDeliveryDateException(
                    "Delivery date must be at least " + MIN_DELIVERY_LEAD_DAYS + " days from today");
        }
        if (deliveryDate.isAfter(today.plusDays(MAX_DELIVERY_LEAD_DAYS))) {
            throw new InvalidDeliveryDateException(
                    "Delivery date must be within the next " + MAX_DELIVERY_LEAD_DAYS + " days");
        }
    }

    private void validateShippingAddress(AddressSnapshotDTO address) {
        if (!"bogota".equals(normalizeText(address.getCity()))) {
            throw new InvalidAddressException("We only ship to Bogota DC");
        }

        if (!"colombia".equals(normalizeText(address.getCountry()))) {
            throw new InvalidAddressException("Invalid country");
        }

        if (address.getZipCode() < 110000 || address.getZipCode() > 119999) {
            throw new InvalidAddressException("Zip code must have 6 digits and start with 11");
        }
    }

    private String normalizeText(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private String generateUniquePublicId() {
        String publicId;

        do {
            publicId = generatePublicId();
        } while (orderRepository.existsByPublicId(publicId));

        return publicId;
    }

    private String generatePublicId() {
        StringBuilder publicId = new StringBuilder(PUBLIC_ID_LENGTH);

        for (int i = 0; i < PUBLIC_ID_LENGTH; i++) {
            int index = RANDOM.nextInt(PUBLIC_ID_CHARS.length());
            publicId.append(PUBLIC_ID_CHARS.charAt(index));
        }

        return publicId.toString();
    }
}
