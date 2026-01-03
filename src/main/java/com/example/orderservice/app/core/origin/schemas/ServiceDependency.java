package com.example.orderservice.app.core.origin.schemas;

import com.example.orderservice.app.core.origin.interfaces.UsecaseAuthorizationService;

/**
 * Container for service dependencies.
 * Encapsulates all common dependencies required by services.
 *
 * @param authorizationService The service used for user authorization
 */
public record ServiceDependency(
        UsecaseAuthorizationService authorizationService) {
}
