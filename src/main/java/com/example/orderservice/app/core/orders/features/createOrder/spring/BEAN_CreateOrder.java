package com.example.orderservice.app.core.orders.features.createOrder.spring;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.OUTPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.services.SERVICE_CreateOrder;
import com.example.orderservice.app.core.origin.interfaces.UsecaseAuthorizationService;
import com.example.orderservice.app.core.origin.spring.AbstractBean;

@Service
public class BEAN_CreateOrder
        extends AbstractBean<INPUT_CreateOrder, OUTPUT_CreateOrder, SERVICE_CreateOrder> {

    private final Map<String, INTERFACE_HELPER_CreateOrder> helpers;

    public BEAN_CreateOrder(
            UsecaseAuthorizationService authorizationService,
            Map<String, INTERFACE_HELPER_CreateOrder> helpers) {
        super(authorizationService);
        this.helpers = helpers;
    }

    @Override
    protected SERVICE_CreateOrder createService() {
        return new SERVICE_CreateOrder(dependencies, helpers);
    }
}
