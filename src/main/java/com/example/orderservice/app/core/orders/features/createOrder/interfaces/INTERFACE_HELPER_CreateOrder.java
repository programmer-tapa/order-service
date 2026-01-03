package com.example.orderservice.app.core.orders.features.createOrder.interfaces;

public interface INTERFACE_HELPER_CreateOrder {
    String saveOrder(String customerId, String items, double totalAmount);

    void publishEvent(String orderId, String customerId, String items, double totalAmount);
}
