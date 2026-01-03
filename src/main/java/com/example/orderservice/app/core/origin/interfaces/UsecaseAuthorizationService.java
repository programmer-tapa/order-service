package com.example.orderservice.app.core.origin.interfaces;

import com.example.orderservice.app.core.origin.schemas.User;

/**
 * Interface for usecase authorization service.
 * Implementations should provide authorization logic for services.
 */
public interface UsecaseAuthorizationService {

    /**
     * Check if the user is authorized to access the specified service.
     *
     * @param user        The user to authorize
     * @param serviceName The name of the service being accessed
     * @return true if authorized, false otherwise
     */
    boolean isAuthorized(User user, String serviceName);
}