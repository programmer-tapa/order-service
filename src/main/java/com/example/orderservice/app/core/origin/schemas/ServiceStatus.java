package com.example.orderservice.app.core.origin.schemas;

/**
 * Enum representing the status of service execution.
 */
public enum ServiceStatus {
    SUCCESS,
    FAILURE,
    UNAUTHORIZED,
    NOT_FOUND,
    VALIDATION_ERROR,
    CONFLICT,
    INTERNAL_ERROR
}
