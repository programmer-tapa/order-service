package com.example.orderservice.app.core.origin.entities;

/**
 * Abstract base class for all use cases in the application.
 * 
 * <p>
 * Use cases encapsulate the business logic of the application and represent
 * a single action or operation that can be executed. Each concrete use case
 * should extend this class and implement the {@link #execute(Object)} method.
 * </p>
 * 
 * <p>
 * This follows the Clean Architecture pattern where use cases are the
 * application-specific business rules that orchestrate the flow of data
 * to and from entities.
 * </p>
 * 
 * @param <I> The input type for the use case (request/command)
 * @param <O> The output type for the use case (response/result)
 * 
 * @see AbstractService
 */
public abstract class AbstractUsecase<I, O> {

    /**
     * Executes the use case with the given input.
     * 
     * <p>
     * This method contains the core business logic of the use case.
     * Implementations should be pure and focus solely on business rules,
     * avoiding any framework or infrastructure concerns.
     * </p>
     * 
     * @param input The input data required to execute the use case
     * @return The result of the use case execution
     * @throws RuntimeException if the use case execution fails
     */
    public abstract O execute(I input);

}
