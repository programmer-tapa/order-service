package com.example.orderservice.app.core.orders.features.createOrder.schemas;

public record INPUT_CreateOrder(String customerId, String items, double totalAmount) {
}
