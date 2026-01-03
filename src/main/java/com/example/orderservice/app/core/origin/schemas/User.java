package com.example.orderservice.app.core.origin.schemas;

/**
 * User context for authorization.
 */
public record User(
        String id,
        String email,
        String role) {
}
