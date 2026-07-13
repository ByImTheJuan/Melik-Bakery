package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderItem;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.repository.OrderRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldRetrieveAndUpdateOrderStatusByPublicId() {
        Order savedOrder = saveOrder("LMN456", OrderStatus.CREATED);

        OrderResponseDTO found = orderService.getOrderById(savedOrder.getPublicId());
        OrderResponseDTO updated = orderService.updateOrderStatus(savedOrder.getPublicId(), OrderStatus.DELIVERED);

        assertThat(found.getClientFirstName()).isEqualTo("Felipe");
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(orderRepository.findByPublicId("LMN456").orElseThrow().getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    private Order saveOrder(String publicId, OrderStatus status) {
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
        order.setPublicId(publicId);
        order.setItems(new ArrayList<>(List.of(new OrderItem(savedProduct, 2))));
        order.setStatus(status);

        return orderRepository.save(order);
    }
}
