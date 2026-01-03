package com.example.orderservice.app.core.origin.schemas;

/**
 * Generic service output wrapper with status, data, and error message.
 *
 * @param <O> The usecase output type
 */
public record ServiceOutput<O>(
        ServiceStatus status,
        O data,
        String errorMessage) {
    /**
     * Create a successful response.
     */
    public static <O> ServiceOutput<O> success(O data) {
        return new ServiceOutput<>(ServiceStatus.SUCCESS, data, null);
    }

    /**
     * Create a failure response.
     */
    public static <O> ServiceOutput<O> failure(String errorMessage) {
        return new ServiceOutput<>(ServiceStatus.FAILURE, null, errorMessage);
    }

    /**
     * Create an unauthorized response.
     */
    public static <O> ServiceOutput<O> unauthorized(String errorMessage) {
        return new ServiceOutput<>(ServiceStatus.UNAUTHORIZED, null, errorMessage);
    }

    /**
     * Create a not found response.
     */
    public static <O> ServiceOutput<O> notFound(String errorMessage) {
        return new ServiceOutput<>(ServiceStatus.NOT_FOUND, null, errorMessage);
    }

    /**
     * Create a validation error response.
     */
    public static <O> ServiceOutput<O> validationError(String errorMessage) {
        return new ServiceOutput<>(ServiceStatus.VALIDATION_ERROR, null, errorMessage);
    }

    /**
     * Create a conflict response.
     */
    public static <O> ServiceOutput<O> conflict(String errorMessage) {
        return new ServiceOutput<>(ServiceStatus.CONFLICT, null, errorMessage);
    }

    /**
     * Create an internal error response.
     */
    public static <O> ServiceOutput<O> internalError(String errorMessage) {
        return new ServiceOutput<>(ServiceStatus.INTERNAL_ERROR, null, errorMessage);
    }
}
