package com.example.orderservice.app.core.orders.features.createOrder.exceptions;

import com.example.orderservice.app.core.origin.exceptions.AppException;
import com.example.orderservice.app.core.origin.schemas.ServiceStatus;

public class InvalidOrderException extends AppException {
    public InvalidOrderException(String message) {
        super(ServiceStatus.VALIDATION_ERROR, message);
    }
}
