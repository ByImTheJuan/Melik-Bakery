package com.hyd.pipes_bakery_backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndFindOrderByPublicId() {
        Product product = new Product();
        product.setName("Croissant");
        product.setPrice(new BigDecimal("9500"));
        product.setDescription("Mantequilla");
        product.setIngredients(List.of("Harina", "Mantequilla"));
        product.setImageUrl("/images/products/croissant.jpg");
        Product savedProduct = productRepository.save(product);

        Order order = new Order(
                "Felipe",
                "Hernandez",
                "felipe@melik.com",
                "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura"
        );
        order.setPublicId("ZX81QW");
        order.setItems(List.of(new OrderItem(savedProduct, 2)));

        Order savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(orderRepository.existsByPublicId("ZX81QW")).isTrue();

        Order found = orderRepository.findByPublicId("ZX81QW").orElseThrow();

        assertThat(found.getClientFirstName()).isEqualTo("Felipe");
        assertThat(found.getTotalAmount()).isEqualByComparingTo(new BigDecimal("19000"));
        assertThat(found.getItems()).hasSize(1);
    }

    @Test
    void shouldFindOrdersByStatusAndCreatedAtBefore() {
        Order expiredOrder = buildBareOrder("OLD001", OrderStatus.PAYMENT_PENDING);
        ReflectionTestUtils.setField(expiredOrder, "createdAt", LocalDateTime.now().minusHours(2));
        orderRepository.save(expiredOrder);

        Order recentOrder = buildBareOrder("NEW001", OrderStatus.PAYMENT_PENDING);
        orderRepository.save(recentOrder);

        Order paidOrder = buildBareOrder("PAID01", OrderStatus.PAID);
        ReflectionTestUtils.setField(paidOrder, "createdAt", LocalDateTime.now().minusHours(2));
        orderRepository.save(paidOrder);

        List<Order> result = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatus.PAYMENT_PENDING, LocalDateTime.now().minusHours(1));

        assertThat(result).extracting(Order::getPublicId).containsExactly("OLD001");
    }

    private Order buildBareOrder(String publicId, OrderStatus status) {
        Order order = new Order(
                "Felipe",
                "Hernandez",
                "felipe@melik.com",
                "3001234567",
                new AddressSnapshot("Calle 123", "Apto 1", "Bogota", 110111, "Colombia"),
                "Laura"
        );
        order.setPublicId(publicId);
        order.setStatus(status);
        return order;
    }
}
