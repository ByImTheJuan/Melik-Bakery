package com.hyd.pipes_bakery_backend.notification;

import com.hyd.pipes_bakery_backend.model.OrderStatus;

/** Published when an order is created or changes status; carries only ids so it is safe to handle async. */
public record OrderStatusChangedEvent(String orderPublicId, OrderStatus status) {
}
