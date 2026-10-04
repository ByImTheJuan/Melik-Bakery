package com.hyd.pipes_bakery_backend.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.order.OrderStatusUpdateRequestDTO;
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldListOrdersAndPatchOrderStatus() throws Exception {
        Order savedOrder = saveOrder("ABC123", OrderStatus.PAID);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("ABC123"))
                .andExpect(jsonPath("$[0].status").value("PAID"));

        OrderStatusUpdateRequestDTO request = new OrderStatusUpdateRequestDTO();
        request.setStatus(OrderStatus.PREPARING);

        mockMvc.perform(patch("/api/orders/{orderId}/status", savedOrder.getPublicId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ABC123"))
                .andExpect(jsonPath("$.status").value("PREPARING"));

        mockMvc.perform(get("/api/orders/{orderId}", savedOrder.getPublicId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"))
                .andExpect(jsonPath("$.clientFirstName").value("Felipe"));

        assertThat(orderRepository.findByPublicId("ABC123").orElseThrow().getStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    void shouldRejectSkippedStatusTransition() throws Exception {
        Order savedOrder = saveOrder("XYZ999", OrderStatus.PAID);

        OrderStatusUpdateRequestDTO request = new OrderStatusUpdateRequestDTO();
        request.setStatus(OrderStatus.DELIVERED);

        mockMvc.perform(patch("/api/orders/{orderId}/status", savedOrder.getPublicId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(orderRepository.findByPublicId("XYZ999").orElseThrow().getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldListTheMostRecentOrdersFirst() throws Exception {
        // Saved out of chronological order on purpose
        saveOrderCreatedAt("MID111", java.time.LocalDateTime.of(2026, 10, 2, 12, 0));
        saveOrderCreatedAt("NEW111", java.time.LocalDateTime.of(2026, 10, 3, 9, 30));
        saveOrderCreatedAt("OLD111", java.time.LocalDateTime.of(2026, 9, 28, 18, 0));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("NEW111"))
                .andExpect(jsonPath("$[1].id").value("MID111"))
                .andExpect(jsonPath("$[2].id").value("OLD111"));
    }

    private Order saveOrderCreatedAt(String publicId, java.time.LocalDateTime createdAt) {
        return saveOrder(publicId, OrderStatus.PAID, createdAt);
    }

    private Order saveOrder(String publicId, OrderStatus status) {
        return saveOrder(publicId, status, null);
    }

    private Order saveOrder(String publicId, OrderStatus status, java.time.LocalDateTime createdAt) {
        Product product = new Product();
        product.setName("Croissant");
        product.setDescription("Mantequilla");
        product.setPrice(new BigDecimal("9500"));
        product.setIngredients(List.of("Harina", "Mantequilla"));
        product.setImageFile("croissant.jpg");
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
        if (createdAt != null) {
            // createdAt is set by the constructor, has no setter and can't be updated after the insert
            org.springframework.test.util.ReflectionTestUtils.setField(order, "createdAt", createdAt);
        }

        return orderRepository.save(order);
    }
}
