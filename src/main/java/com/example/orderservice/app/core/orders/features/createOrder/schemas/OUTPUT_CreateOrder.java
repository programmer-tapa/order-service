package com.example.orderservice.app.core.orders.features.createOrder.schemas;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Output schema for create order response.
 */
public record OUTPUT_CreateOrder(
        String orderId,
        String status,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt) {
}
