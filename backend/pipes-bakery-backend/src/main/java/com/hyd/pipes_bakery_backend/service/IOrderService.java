package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.model.OrderStatus;

public interface IOrderService {

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(@NonNull Long orderId);

    OrderResponseDTO cancelOrder(@NonNull Long orderId);

    OrderResponseDTO updateOrderStatus(@NonNull Long orderId, OrderStatus status);

    OrderResponseDTO checkout(Long clientId, CheckoutOrderRequestDTO request);
}
