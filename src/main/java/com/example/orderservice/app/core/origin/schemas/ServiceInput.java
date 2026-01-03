package com.example.orderservice.app.core.origin.schemas;

/**
 * Generic service input wrapper that encapsulates user context and usecase
 * input.
 *
 * @param <I> The usecase input type
 */
public record ServiceInput<I>(
        User user,
        I data) {
}
