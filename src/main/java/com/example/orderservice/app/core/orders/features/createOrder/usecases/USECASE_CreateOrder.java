package com.example.orderservice.app.core.orders.features.createOrder.usecases;

import java.time.ZoneOffset;

import com.example.orderservice.app.core.orders.entities.Order;
import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.OUTPUT_CreateOrder;
import com.example.orderservice.app.core.origin.entities.AbstractUsecase;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class USECASE_CreateOrder extends AbstractUsecase<INPUT_CreateOrder, OUTPUT_CreateOrder> {

    private final INTERFACE_HELPER_CreateOrder helper;

    @Override
    public OUTPUT_CreateOrder execute(INPUT_CreateOrder input) {
        // Validate input
        helper.validateInput(input);

        // Build Order entity from input
        Order order = helper.buildOrder(input);

        // Save order using helper
        Order savedOrder = helper.saveOrder(order);

        // Publish event
        helper.publishEvent(savedOrder);

        // Return output
        return new OUTPUT_CreateOrder(
                savedOrder.getId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount(),
                savedOrder.getCurrency(),
                savedOrder.getCreatedAt().toInstant(ZoneOffset.UTC));
    }
}
