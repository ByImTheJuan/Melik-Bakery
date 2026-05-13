package com.hyd.pipes_bakery_backend.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_FAILED,
    PAID,
    PREPARING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
