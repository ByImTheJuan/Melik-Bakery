package com.hyd.pipes_bakery_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.payment.CheckoutSnapshot;
import com.hyd.pipes_bakery_backend.model.Order;
import com.hyd.pipes_bakery_backend.model.OrderStatus;

public interface IOrderService {

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(@NonNull String orderId);

    OrderResponseDTO cancelOrder(@NonNull String orderId);

    OrderResponseDTO updateOrderStatus(@NonNull String orderId, OrderStatus status);

    /** Validates the checkout data and cart, and captures them for a payment attempt. Creates nothing. */
    CheckoutSnapshot buildCheckoutSnapshot(UUID cartId, CheckoutOrderRequestDTO request);

    /** Creates the order for an approved payment, already in PAID status. */
    Order createPaidOrder(CheckoutSnapshot snapshot);
}
