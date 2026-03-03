package com.hyd.pipes_bakery_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.model.OrderStatus;
import com.hyd.pipes_bakery_backend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //GET /api/orders
    @GetMapping()
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    //GET /api/orders/{orderId}
    @GetMapping("/{orderId}")
    public OrderResponseDTO getOrderById(@NonNull @PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    //PUT /api/orders/{orderId}
    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateOrderStatus(
            @NonNull @PathVariable Long orderId,
            @PathVariable OrderStatus status) {

        orderService.updateOrderStatus(orderId, status);
    }
}