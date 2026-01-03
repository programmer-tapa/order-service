package com.example.orderservice.app.core.orders.features.createOrder.contracts;

import org.springframework.stereotype.Component;

import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
import com.example.orderservice.app.infra.logger.interfaces.LoggerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CONTRACT_HELPER_CreateOrder_V0 implements INTERFACE_HELPER_CreateOrder {

    private final LoggerService loggerService;

    @Override
    public String saveOrder(String customerId, String items, double totalAmount) {
        // Here you could use jdbcTemplate to save to DB
        String orderId = java.util.UUID.randomUUID().toString();
        loggerService.info("Creating order: " + orderId + " for customer " + customerId + " with items: " + items
                + ", total: " + totalAmount);
        return orderId;
    }

    @Override
    public void publishEvent(String orderId, String customerId, String items, double totalAmount) {
        // TODO: Inject KafkaTemplate and publish to Kafka topic
        loggerService.info("Publishing OrderCreated event to Kafka - orderId: " + orderId
                + ", customerId: " + customerId + ", items: " + items + ", total: " + totalAmount);
    }
}
