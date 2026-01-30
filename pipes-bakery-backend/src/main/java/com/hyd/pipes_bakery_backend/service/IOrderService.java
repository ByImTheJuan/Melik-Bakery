package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;

public interface IOrderService {

    OrderResponseDTO createOrder(Long clientId);

    OrderResponseDTO getOrderById(Long orderId);

    List<OrderResponseDTO> getOrdersByClient(Long clientId);

    OrderResponseDTO cancelOrder(Long orderId);

    OrderStatus updateOrderStatus(Long orderId, OrderStatus status);

    OrderResponseDTO toDto(Order order);

    Order toEntity(OrderResponseDTO dto);
}
