package com.hyd.pipes_bakery_backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderStatusUpdateRequestDTO;
import com.hyd.pipes_bakery_backend.dto.orderItem.OrderItemResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.service.OrderService;

@SuppressWarnings("null")
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldGetAllOrdersSuccessfully() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(buildOrderResponse("ABC123", OrderStatus.CREATED)));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("ABC123"))
                .andExpect(jsonPath("$[0].status").value("CREATED"));

        verify(orderService).getAllOrders();
    }

    @Test
    void shouldGetOrderByIdSuccessfully() throws Exception {
        when(orderService.getOrderById("ABC123")).thenReturn(buildOrderResponse("ABC123", OrderStatus.CREATED));

        mockMvc.perform(get("/api/orders/{orderId}", "ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("ABC123"))
                .andExpect(jsonPath("$.clientFirstName").value("Felipe"));

        verify(orderService).getOrderById("ABC123");
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        when(orderService.getOrderById("MISSING"))
                .thenThrow(new ResourceNotFoundException("Order not found with id MISSING"));

        mockMvc.perform(get("/api/orders/{orderId}", "MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id MISSING"));

        verify(orderService).getOrderById("MISSING");
    }

    @Test
    void shouldPatchOrderStatusSuccessfully() throws Exception {
        OrderStatusUpdateRequestDTO request = new OrderStatusUpdateRequestDTO();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderService.updateOrderStatus("ABC123", OrderStatus.SHIPPED))
                .thenReturn(buildOrderResponse("ABC123", OrderStatus.SHIPPED));

        mockMvc.perform(patch("/api/orders/{orderId}/status", "ABC123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("ABC123"))
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        verify(orderService).updateOrderStatus("ABC123", OrderStatus.SHIPPED);
    }

    @Test
    void shouldReturnBadRequestWhenStatusIsMissing() throws Exception {
        mockMvc.perform(patch("/api/orders/{orderId}/status", "ABC123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenPatchingNonExistingOrder() throws Exception {
        OrderStatusUpdateRequestDTO request = new OrderStatusUpdateRequestDTO();
        request.setStatus(OrderStatus.CANCELLED);

        when(orderService.updateOrderStatus("MISSING", OrderStatus.CANCELLED))
                .thenThrow(new ResourceNotFoundException("Order not found with id MISSING"));

        mockMvc.perform(patch("/api/orders/{orderId}/status", "MISSING")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id MISSING"));

        verify(orderService).updateOrderStatus("MISSING", OrderStatus.CANCELLED);
    }

    private OrderResponseDTO buildOrderResponse(String id, OrderStatus status) {
        return new OrderResponseDTO(
                id,
                "Felipe",
                "Hernandez",
                "felipe@melik.com",
                "3001234567",
                List.of(new OrderItemResponseDTO(1L, 10L, "Croissant", 2, new BigDecimal("9500"))),
                new BigDecimal("19000"),
                status,
                LocalDateTime.of(2026, 4, 30, 12, 0),
                new AddressSnapshotDTO("Calle 123", "Apto 1", "Bogota", 110111, "Colombia")
        );
    }
}
