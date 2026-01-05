package com.example.orderservice.app.core.orders.features.createOrder.interfaces;

import com.example.orderservice.app.core.orders.entities.Order;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;

public interface INTERFACE_HELPER_CreateOrder {

    /**
     * Validates the input for creating an order.
     * 
     * @param input The input to validate
     * @throws InvalidOrderException if validation fails
     */
    void validateInput(INPUT_CreateOrder input);

    /**
     * Builds an Order entity from the input.
     * 
     * @param input The input to build the order from
     * @return The built Order entity
     */
    Order buildOrder(INPUT_CreateOrder input);

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
