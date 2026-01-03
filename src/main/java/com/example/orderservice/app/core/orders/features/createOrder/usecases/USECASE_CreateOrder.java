package com.example.orderservice.app.core.orders.features.createOrder.usecases;

import com.example.orderservice.app.core.orders.features.createOrder.exceptions.InvalidOrderException;
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
        if (input.customerId() == null || input.customerId().isBlank()) {
            throw new InvalidOrderException("Customer ID is required");
        }
        if (input.totalAmount() <= 0) {
            throw new InvalidOrderException("Total amount must be greater than zero");
        }

        String orderId = helper.saveOrder(input.customerId(), input.items(), input.totalAmount());

        helper.publishEvent(orderId, input.customerId(), input.items(), input.totalAmount());

        return new OUTPUT_CreateOrder(orderId, "CREATED");
    }
}
