package com.example.orderservice.app.core.orders.features.createOrder.services;

import java.util.Map;

import com.example.orderservice.app.core.orders.features.createOrder.interfaces.INTERFACE_HELPER_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.INPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.schemas.OUTPUT_CreateOrder;
import com.example.orderservice.app.core.orders.features.createOrder.usecases.USECASE_CreateOrder;
import com.example.orderservice.app.core.origin.entities.AbstractService;
import com.example.orderservice.app.core.origin.entities.AbstractUsecase;
import com.example.orderservice.app.core.origin.schemas.ServiceDependency;

public class SERVICE_CreateOrder extends AbstractService<INPUT_CreateOrder, OUTPUT_CreateOrder> {

    private static final String SERVICE_NAME = "Orders.CreateOrder";
    private final Map<String, INTERFACE_HELPER_CreateOrder> helpers;

    public SERVICE_CreateOrder(ServiceDependency dependencies,
            Map<String, INTERFACE_HELPER_CreateOrder> helpers) {
        super(dependencies);
        this.helpers = helpers;
    }

    @Override
    protected String detectServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected AbstractUsecase<INPUT_CreateOrder, OUTPUT_CreateOrder> build(INPUT_CreateOrder input) {
        String helperKey = "CONTRACT_HELPER_CreateOrder_V0";
        INTERFACE_HELPER_CreateOrder usecaseHelper = helpers.get(helperKey);

        if (usecaseHelper == null) {
            throw new RuntimeException("Helper not found: " + helperKey);
        }

        return new USECASE_CreateOrder(usecaseHelper);
    }
}
