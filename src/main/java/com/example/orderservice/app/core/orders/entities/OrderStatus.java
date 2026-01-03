package com.example.orderservice.app.core.orders.entities;

/**
 * Enum representing the possible statuses of an order.
 */
public enum OrderStatus {
    CREATED,
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
