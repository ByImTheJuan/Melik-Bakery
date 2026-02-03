package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.model.OrderStatus;

public interface IOrderService {

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(Long orderId);

    OrderResponseDTO cancelOrder(Long orderId);

    OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status);

    OrderResponseDTO checkout(Long clientId, CheckoutOrderRequestDTO request);
}
