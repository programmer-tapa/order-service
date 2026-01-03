package com.example.orderservice.app.core.orders.features.createOrder.interfaces;

import com.example.orderservice.app.core.orders.entities.Order;

public interface INTERFACE_HELPER_CreateOrder {

    /**
     * Saves an order to the database.
     * 
     * @param order The order entity to save
     * @return The saved order with generated ID
     */
    Order saveOrder(Order order);

    /**
     * Publishes an OrderCreated event.
     * 
     * @param order The order that was created
     */
    void publishEvent(Order order);
}
