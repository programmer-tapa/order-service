package com.example.orderservice.framework.entrypoints.api.orders.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.OUTPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.spring.BEAN_CreateOrder;
import com.example.orderservice.app.core.origin.schemas.ServiceOutput;
import com.example.orderservice.app.core.origin.schemas.User;
import com.example.orderservice.app.core.origin.spring.ControllerServiceExecutor;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v0/orders")
@AllArgsConstructor
public class OrdersController {

    private final ControllerServiceExecutor controllerServiceExecutor;

    private final BEAN_CreateOrder beanCreateOrder;

    /**
     * Endpoint to create a new order.
     * 
     * POST /api/v0/orders/create
     * Request body: { "customerId": "123", "items": "item1,item2", "totalAmount":
     * 99.99 }
     * Response: { "status": "SUCCESS", "data": { "orderId": "...", "status":
     * "CREATED" }, "errorMessage": null }
     * 
     * @param input the input containing order details
     * @return ResponseEntity with the service output
     */
    @PostMapping("/create")
    public ResponseEntity<ServiceOutput<OUTPUT_CreateOrder>> createOrder(
            @RequestBody INPUT_CreateOrder input) {

        // TODO: In a real application, extract user from authentication context
        User user = new User("1", "api-user@example.com", "USER");

        return controllerServiceExecutor.execute(
                beanCreateOrder.getService()::run,
                input,
                user);
    }
}
