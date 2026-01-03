package com.example.orderservice.app.core.orders.features.createOrder.usecases;

import java.time.ZoneOffset;

import com.example.orderservice.app.core.orders.entities.Order;
import com.example.orderservice.app.core.orders.entities.OrderItem;
import com.example.orderservice.app.core.orders.features.createOrder.exceptions.InvalidOrderException;
import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.InputOrderItem;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.OUTPUT_CreateOrder;
import com.example.orderservice.app.core.origin.entities.AbstractUsecase;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class USECASE_CreateOrder extends AbstractUsecase<INPUT_CreateOrder, OUTPUT_CreateOrder> {

    private final INTERFACE_HELPER_CreateOrder helper;

    @Override
    public OUTPUT_CreateOrder execute(INPUT_CreateOrder input) {
        // Validate input
        validateInput(input);

        // Build Order entity from input
        Order order = buildOrder(input);

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

    private void validateInput(INPUT_CreateOrder input) {
        if (input.customerId() == null || input.customerId().isBlank()) {
            throw new InvalidOrderException("Customer ID is required");
        }
        if (input.items() == null || input.items().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }
        if (input.currency() == null || input.currency().isBlank()) {
            throw new InvalidOrderException("Currency is required");
        }

        // Validate each item
        for (InputOrderItem item : input.items()) {
            if (item.productId() == null || item.productId().isBlank()) {
                throw new InvalidOrderException("Product ID is required for all items");
            }
            if (item.quantity() <= 0) {
                throw new InvalidOrderException("Quantity must be greater than zero");
            }
            if (item.unitPrice() == null || item.unitPrice().signum() <= 0) {
                throw new InvalidOrderException("Unit price must be greater than zero");
            }
        }
    }

    private Order buildOrder(INPUT_CreateOrder input) {
        Order order = new Order();
        order.setCustomerId(input.customerId());
        order.setCurrency(input.currency());

        // Convert input items to OrderItem entities
        for (InputOrderItem inputItem : input.items()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(inputItem.productId());
            orderItem.setQuantity(inputItem.quantity());
            orderItem.setUnitPrice(inputItem.unitPrice());
            orderItem.setTotalPrice(inputItem.unitPrice().multiply(java.math.BigDecimal.valueOf(inputItem.quantity())));
            order.addItem(orderItem);
        }

        return order;
    }
}
