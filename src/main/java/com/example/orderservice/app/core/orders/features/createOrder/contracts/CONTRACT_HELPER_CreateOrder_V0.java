package com.example.orderservice.app.core.orders.features.createOrder.contracts;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.orderservice.app.core.orders.entities.Order;
import com.example.orderservice.app.core.orders.entities.OrderItem;
import com.example.orderservice.app.core.orders.features.createOrder.exceptions.InvalidOrderException;
import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.InputOrderItem;
import com.example.orderservice.app.infra.events.entities.Event;
import com.example.orderservice.app.infra.events.interfaces.EventService;
import com.example.orderservice.app.infra.logger.interfaces.LoggerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CONTRACT_HELPER_CreateOrder_V0 implements INTERFACE_HELPER_CreateOrder {

    private final LoggerService loggerService;
    private final EventService eventService;

    @Override
    public void validateInput(INPUT_CreateOrder input) {
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

    @Override
    public Order buildOrder(INPUT_CreateOrder input) {
        Order order = new Order();
        order.setCustomerId(input.customerId());
        order.setCurrency(input.currency());

        // Convert input items to OrderItem entities
        for (InputOrderItem inputItem : input.items()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(inputItem.productId());
            orderItem.setQuantity(inputItem.quantity());
            orderItem.setUnitPrice(inputItem.unitPrice());
            orderItem.setTotalPrice(inputItem.unitPrice().multiply(BigDecimal.valueOf(inputItem.quantity())));
            order.addItem(orderItem);
        }

        return order;
    }

    @Override
    public Order saveOrder(Order order) {
        // Generate UUID for the order
        // String orderId = UUID.randomUUID().toString();
        String orderId = "2";
        order.setId(orderId);

        // Generate UUIDs for each order item
        for (OrderItem item : order.getItems()) {
            item.setId(UUID.randomUUID().toString());
            item.setOrderId(orderId);
        }

        // Here you could use jdbcTemplate to save to DB
        loggerService.info("Creating order: " + orderId + " for customer " + order.getCustomerId()
                + " with " + order.getItems().size() + " items, total: " + order.getTotalAmount()
                + " " + order.getCurrency());

        return order;
    }

    @Override
    public void publishEvent(Order order) {
        Map<String, Object> eventData = Map.of(
                "orderId", order.getId(),
                "customerId", order.getCustomerId(),
                "items", order.getItems().stream()
                        .map(item -> Map.of(
                                "productId", item.getProductId(),
                                "quantity", item.getQuantity(),
                                "unitPrice", item.getUnitPrice(),
                                "totalPrice", item.getTotalPrice()))
                        .collect(Collectors.toList()),
                "totalAmount", order.getTotalAmount(),
                "currency", order.getCurrency(),
                "status", order.getStatus().name(),
                "createdAt", order.getCreatedAt().toString());

        Event event = new Event(order.getId(), "OrderCreated", eventData);
        eventService.publishEvent(event);

        loggerService.info("Published OrderCreated event to Kafka - orderId: " + order.getId());
    }
}
