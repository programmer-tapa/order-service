package com.example.orderservice.app.core.orders.features.createOrder.schemas;

import java.util.List;

/**
 * Input schema for creating an order.
 */
public record INPUT_CreateOrder(
        String customerId,
        List<InputOrderItem> items,
        String currency) {
}
