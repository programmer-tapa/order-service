package com.example.orderservice.app.core.origin.spring;

import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.orderservice.app.core.origin.schemas.ServiceInput;
import com.example.orderservice.app.core.origin.schemas.ServiceOutput;
import com.example.orderservice.app.core.origin.schemas.User;

/**
 * Generic controller service executor that abstracts the common pattern of:
 * 1. Creating ServiceInput from user and input data
 * 2. Executing the service
 * 3. Mapping ServiceStatus to HTTP status codes
 * 4. Returning a proper ResponseEntity
 * 
 * This eliminates boilerplate code in controllers.
 */
@Component
public class ControllerServiceExecutor {

    /**
     * Executes a service operation and returns an appropriate ResponseEntity.
     *
     * @param <I>           the input type
     * @param <O>           the output type
     * @param serviceRunner function that takes ServiceInput and returns
     *                      ServiceOutput
     * @param input         the input data for the service
     * @param user          the user context for authorization
     * @return ResponseEntity with appropriate HTTP status based on service result
     */
    public <I, O> ResponseEntity<ServiceOutput<O>> execute(
            Function<ServiceInput<I>, ServiceOutput<O>> serviceRunner,
            I input,
            User user) {

        ServiceInput<I> serviceInput = new ServiceInput<>(user, input);
        ServiceOutput<O> output = serviceRunner.apply(serviceInput);

        HttpStatus status = mapStatus(output);
        return new ResponseEntity<>(output, status);
    }

    /**
     * Executes a service operation with a pre-built ServiceInput.
     * Useful when ServiceInput is already constructed.
     *
     * @param <I>           the input type
     * @param <O>           the output type
     * @param serviceRunner function that takes ServiceInput and returns
     *                      ServiceOutput
     * @param serviceInput  the pre-built service input
     * @return ResponseEntity with appropriate HTTP status based on service result
     */
    public <I, O> ResponseEntity<ServiceOutput<O>> execute(
            Function<ServiceInput<I>, ServiceOutput<O>> serviceRunner,
            ServiceInput<I> serviceInput) {

        ServiceOutput<O> output = serviceRunner.apply(serviceInput);
        HttpStatus status = mapStatus(output);
        return new ResponseEntity<>(output, status);
    }

    /**
     * Maps ServiceStatus to HTTP status codes.
     *
     * @param <O>    the output type
     * @param output the service output containing status
     * @return corresponding HttpStatus
     */
    private <O> HttpStatus mapStatus(ServiceOutput<O> output) {
        return switch (output.status()) {
            case SUCCESS -> HttpStatus.OK;
            case UNAUTHORIZED -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case CONFLICT -> HttpStatus.CONFLICT;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case FAILURE -> HttpStatus.BAD_REQUEST;
        };
    }
}
