package com.example.orderservice.app.core.origin.entities;

import com.example.orderservice.app.core.origin.exceptions.AppException;
import com.example.orderservice.app.core.origin.interfaces.UsecaseAuthorizationService;
import com.example.orderservice.app.core.origin.schemas.ServiceDependency;
import com.example.orderservice.app.core.origin.schemas.ServiceInput;
import com.example.orderservice.app.core.origin.schemas.ServiceOutput;
import com.example.orderservice.app.core.origin.schemas.User;

/**
 * Abstract service that handles authorization and usecase execution.
 *
 * @param <I> Usecase input type
 * @param <O> Usecase output type
 */
public abstract class AbstractService<I, O> {

    protected final UsecaseAuthorizationService authorizationService;

    /**
     * Constructor to inject service dependencies.
     *
     * @param dependencies The container holding all service dependencies
     */
    public AbstractService(ServiceDependency dependencies) {
        this.authorizationService = dependencies.authorizationService();
    }

    /**
     * Detect and return the service name.
     * Used for authorization and logging purposes.
     *
     * @return The unique name/identifier of this service
     */
    protected abstract String detectServiceName();

    /**
     * Build the usecase with its dependencies.
     * 
     * @param input The usecase input, can be used to determine and fetch
     *              the right dependencies based on input values.
     * @return The configured usecase instance
     */
    protected abstract AbstractUsecase<I, O> build(I input);

    /**
     * Authorize the user for this usecase.
     * Uses the authorization service to check if the user is authorized.
     *
     * @param user The user to authorize
     * @return true if authorized, false otherwise
     */
    protected boolean authorize(User user) {
        if (user == null) {
            return false;
        }
        String serviceName = detectServiceName();
        return authorizationService.isAuthorized(user, serviceName);
    }

    /**
     * Run the service with authorization and structured response.
     *
     * @param input The service input containing user and data
     * @return ServiceOutput with status, data, and error message
     */
    public ServiceOutput<O> run(ServiceInput<I> input) {
        // Authorization check
        if (!authorize(input.user())) {
            return ServiceOutput.unauthorized("User is not authorized to perform this action");
        }

        // Build and execute usecase
        try {
            // Decouple service orchestration from business logic by using a factory method
            // to build the usecase, allowing for dynamic dependency injection based on
            // input.
            AbstractUsecase<I, O> usecase = build(input.data());
            O result = usecase.execute(input.data());
            return ServiceOutput.success(result);
        } catch (AppException e) {
            return new ServiceOutput<>(e.getStatus(), null, e.getMessage());
        } catch (Exception e) {
            return ServiceOutput.failure(e.getMessage());
        }
    }
}
