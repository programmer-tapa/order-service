package com.example.orderservice.app.core.origin.exceptions;

import com.example.orderservice.app.core.origin.schemas.ServiceStatus;

/**
 * Base exception for application-specific errors that map to a ServiceStatus.
 */
public abstract class AppException extends RuntimeException {
    private final ServiceStatus status;

    protected AppException(ServiceStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ServiceStatus getStatus() {
        return status;
    }
}
