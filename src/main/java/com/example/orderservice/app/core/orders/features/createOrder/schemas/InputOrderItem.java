package com.example.orderservice.app.core.orders.features.createOrder.schemas;

import java.math.BigDecimal;

/**
 * Schema representing an order item in the create order input.
 */
public record InputOrderItem(
        String productId,
        int quantity,
        BigDecimal unitPrice) {
}
