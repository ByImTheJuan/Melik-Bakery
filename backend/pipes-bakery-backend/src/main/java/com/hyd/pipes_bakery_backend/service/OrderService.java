package com.hyd.pipes_bakery_backend.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.exception.CartIsEmptyException;
import com.hyd.pipes_bakery_backend.exception.InvalidAddressException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.AddressMapper;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@Service
public class OrderService implements IOrderService {

    private static final String PUBLIC_ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PUBLIC_ID_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartStorage cartStorage;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CartStorage cartStorage,
            OrderMapper orderMapper,
            AddressMapper addressMapper
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartStorage = cartStorage;
        this.orderMapper = orderMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
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
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(@NonNull String orderId, OrderStatus status) {
        return orderRepository.findByPublicId(orderId)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                })
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    @Transactional
    @Override
    public OrderResponseDTO checkout(UUID cartId, CheckoutOrderRequestDTO request) {
        validateShippingAddress(request.getShippingAddress());

        ShoppingCart cart = cartStorage.getCart(cartId);
        if (cart.isEmpty()) {
            throw new CartIsEmptyException("Cart is empty. Impossible to checkout");
        }

        Order order = new Order(
                request.getClientFirstName(),
                request.getClientLastName(),
                request.getClientEmail(),
                request.getClientPhoneNumber(),
                addressMapper.toSnapshotEntity(request.getShippingAddress()),
                request.getReceiverName()
        );
        order.setPublicId(generateUniquePublicId());

        List<OrderItem> items = cart.getItems().stream()
                .map(itemDto -> {
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Product not found with id " + itemDto.getProductId()
                                    )
                            );

                    return new OrderItem(product, itemDto.getQuantity(), itemDto.getUnitPriceAtAdd());
                })
                .toList();

        order.setItems(items);
        orderRepository.save(order);
        cartStorage.clearCart(cartId);

        return orderMapper.toDto(order);
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
