package com.example.orderservice.app.core.origin.spring;

import com.example.orderservice.app.core.origin.entities.AbstractService;
import com.example.orderservice.app.core.origin.interfaces.UsecaseAuthorizationService;
import com.example.orderservice.app.core.origin.schemas.ServiceDependency;

/**
 * Abstract base class for Spring beans that wrap services.
 * Provides common functionality for creating service dependencies
 * and exposing the service instance.
 *
 * @param <I> The service input type
 * @param <O> The service output type
 * @param <S> The concrete service type extending AbstractService
 */
public abstract class AbstractBean<I, O, S extends AbstractService<I, O>> {

    protected final UsecaseAuthorizationService authorizationService;
    protected final ServiceDependency dependencies;
    protected S service;

    /**
     * Constructor that sets up common dependencies.
     * Subclasses should call this and then create their specific service.
     *
     * @param authorizationService the authorization service injected by Spring
     */
    protected AbstractBean(UsecaseAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
        this.dependencies = new ServiceDependency(authorizationService);
    }

    /**
     * Factory method for subclasses to create their specific service instance.
     * Called during construction after dependencies are set up.
     *
     * @return the concrete service instance
     */
    protected abstract S createService();

    public S getService() {
        if (this.service == null) {
            this.service = createService();
        }
        return service;
    }
}
