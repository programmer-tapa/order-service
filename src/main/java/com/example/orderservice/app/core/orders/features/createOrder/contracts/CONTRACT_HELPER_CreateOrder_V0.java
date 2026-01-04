package com.example.orderservice.app.core.orders.features.createOrder.contracts;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.orderservice.app.core.orders.entities.Order;
import com.example.orderservice.app.core.orders.entities.OrderItem;
import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
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
