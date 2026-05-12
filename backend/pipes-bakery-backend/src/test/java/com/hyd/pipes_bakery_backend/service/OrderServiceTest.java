package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.exception.CartIsEmptyException;
import com.hyd.pipes_bakery_backend.exception.InvalidAddressException;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.AddressMapper;
import com.hyd.pipes_bakery_backend.mapper.OrderItemMapper;
import com.hyd.pipes_bakery_backend.mapper.OrderMapper;
import com.hyd.pipes_bakery_backend.model.CartItem;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.ShoppingCart;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;
import com.hyd.pipes_bakery_backend.storage.CartStorage;

@SuppressWarnings({"unused", "null"})
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartStorage cartStorage;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        AddressMapper addressMapper = new AddressMapper();
        OrderMapper orderMapper = new OrderMapper(new OrderItemMapper(), addressMapper);
        orderService = new OrderService(orderRepository, productRepository, cartStorage, orderMapper, addressMapper);
    }

    @Test
    void shouldGetAllOrdersSuccessfully() {
        when(orderRepository.findAll()).thenReturn(List.of(buildOrder("ABC123", OrderStatus.CREATED)));

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("ABC123");
    }

    @Test
    void shouldGetOrderByPublicIdSuccessfully() {
        when(orderRepository.findByPublicId("ABC123"))
                .thenReturn(Optional.of(buildOrder("ABC123", OrderStatus.CREATED)));

        OrderResponseDTO result = orderService.getOrderById("ABC123");

        assertThat(result.getId()).isEqualTo("ABC123");
        assertThat(result.getClientFirstName()).isEqualTo("Felipe");
    }

    @Test
    void shouldUpdateOrderStatusSuccessfully() {
        Order order = buildOrder("ABC123", OrderStatus.CREATED);

        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO result = orderService.updateOrderStatus("ABC123", OrderStatus.PAID);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        Order order = buildOrder("ABC123", OrderStatus.CREATED);

        when(orderRepository.findByPublicId("ABC123")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO result = orderService.cancelOrder("ABC123");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingOrder() {
        when(orderRepository.findByPublicId("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus("MISSING", OrderStatus.PAID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Order not found with id MISSING");
    }

    @Test
    void shouldCheckoutSuccessfullyAndClearCart() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart cart = new ShoppingCart(cartId);
        cart.setItems(List.of(new CartItem(1L, "Croissant", 2, new BigDecimal("9500"), "/images/products/croissant.jpg")));

        Product product = new Product();
        product.setId(1L);
        product.setName("Croissant");
        product.setPrice(new BigDecimal("9500"));

        when(cartStorage.getCart(cartId)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.existsByPublicId(anyString())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            if (!savedOrder.getItems().isEmpty()) {
                ReflectionTestUtils.setField(savedOrder.getItems().get(0), "id", 1L);
            }
            return savedOrder;
        });

        OrderResponseDTO result = orderService.checkout(cartId, buildCheckoutRequest("BOGOTÁ", "COLOMBIA", 110111));

        assertThat(result.getId()).matches("[A-Z0-9]{6}");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("19000"));
        verify(cartStorage).clearCart(cartId);
    }

    @Test
    void shouldThrowWhenCheckoutCartIsEmpty() {
        UUID cartId = UUID.randomUUID();
        ShoppingCart cart = new ShoppingCart(cartId);

        when(cartStorage.getCart(cartId)).thenReturn(cart);

        assertThatThrownBy(() -> orderService.checkout(cartId, buildCheckoutRequest("Bogotá", "Colombia", 110111)))
                .isInstanceOf(CartIsEmptyException.class)
                .hasMessage("Cart is empty. Impossible to checkout");
    }

    @Test
    void shouldRejectInvalidZipCodeDuringCheckout() {
        assertThatThrownBy(() -> orderService.checkout(UUID.randomUUID(), buildCheckoutRequest("Bogotá", "Colombia", 100000)))
                .isInstanceOf(InvalidAddressException.class)
                .hasMessage("Zip code must have 6 digits and start with 11");
    }

    @Test
    void shouldRejectInvalidCityDuringCheckout() {
        assertThatThrownBy(() -> orderService.checkout(UUID.randomUUID(), buildCheckoutRequest("Medellín", "Colombia", 110111)))
                .isInstanceOf(InvalidAddressException.class)
                .hasMessage("We only ship to Bogota DC");
    }

    private CheckoutOrderRequestDTO buildCheckoutRequest(String city, String country, int zipCode) {
        CheckoutOrderRequestDTO request = new CheckoutOrderRequestDTO();
        request.setClientFirstName("Felipe");
        request.setClientLastName("Hernandez");
        request.setClientEmail("felipe@melik.com");
        request.setClientPhoneNumber("3001234567");
        request.setReceiverName("Laura");
        request.setShippingAddress(new AddressSnapshotDTO("Calle 123", "Apto 1", city, zipCode, country));
        return request;
    }

    private Order buildOrder(String publicId, OrderStatus status) {
        Product product = new Product();
        product.setId(1L);
        product.setName("Croissant");
        product.setPrice(new BigDecimal("9500"));

        com.hyd.pipes_bakery_backend.model.OrderItem orderItem =
                new com.hyd.pipes_bakery_backend.model.OrderItem(product, 2);
        ReflectionTestUtils.setField(orderItem, "id", 1L);

        Order order = new Order(
                "Felipe",
                "Hernandez",
                "felipe@melik.com",
                "3001234567",
                new com.hyd.pipes_bakery_backend.model.AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura"
        );
        order.setPublicId(publicId);
        order.setItems(new ArrayList<>(List.of(orderItem)));
        order.setStatus(status);
        return order;
    }
}
