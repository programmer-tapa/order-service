package com.example.orderservice.app.infra.logger.interfaces;

import java.util.Map;

/**
 * Production-grade logger service interface for comprehensive logging across
 * the application.
 * Supports multiple log levels, structured logging, and contextual information.
 */
public interface LoggerService {

    /**
     * Log a debug message
     * 
     * @param message the message to log
     */
    void debug(String message);

    /**
     * Log a debug message with additional context
     * 
     * @param message the message to log
     * @param context contextual data as key-value pairs
     */
    void debug(String message, Map<String, Object> context);

    /**
     * Log an info message
     * 
     * @param message the message to log
     */
    void info(String message);

    /**
     * Log an info message with additional context
     * 
     * @param message the message to log
     * @param context contextual data as key-value pairs
     */
    void info(String message, Map<String, Object> context);

    /**
     * Log a warning message
     * 
     * @param message the message to log
     */
    void warn(String message);

    /**
     * Log a warning message with additional context
     * 
     * @param message the message to log
     * @param context contextual data as key-value pairs
     */
    void warn(String message, Map<String, Object> context);

    /**
     * Log an error message
     * 
     * @param message the message to log
     */
    void error(String message);

    /**
     * Log an error message with exception details
     * 
     * @param message   the message to log
     * @param exception the exception to log
     */
    void error(String message, Throwable exception);

    /**
     * Log an error message with exception and additional context
     * 
     * @param message   the message to log
     * @param exception the exception to log
     * @param context   contextual data as key-value pairs
     */
    void error(String message, Throwable exception, Map<String, Object> context);

    /**
     * Log a critical error message
     * 
     * @param message the message to log
     */
    void critical(String message);

    /**
     * Log a critical error message with exception
     * 
     * @param message   the message to log
     * @param exception the exception to log
     */
    void critical(String message, Throwable exception);

    /**
     * Log a critical error with exception and context
     * 
     * @param message   the message to log
     * @param exception the exception to log
     * @param context   contextual data as key-value pairs
     */
    void critical(String message, Throwable exception, Map<String, Object> context);

    /**
     * Set the correlation ID for request tracing
     * 
     * @param correlationId unique identifier for request tracking
     */
    void setCorrelationId(String correlationId);

    /**
     * Get the current correlation ID
     * 
     * @return the correlation ID
     */
    String getCorrelationId();

    /**
     * Add contextual information that will be included in all subsequent logs
     * 
     * @param key   the context key
     * @param value the context value
     */
    void addContext(String key, Object value);

    /**
     * Remove contextual information
     * 
     * @param key the context key to remove
     */
    void removeContext(String key);

    /**
     * Clear all contextual information
     */
    void clearContext();

    /**
     * Log performance metrics
     * 
     * @param operationName   the name of the operation
     * @param executionTimeMs execution time in milliseconds
     * @param success         whether the operation was successful
     */
    void logPerformance(String operationName, long executionTimeMs, boolean success);

    /**
     * Log performance metrics with additional context
     * 
     * @param operationName   the name of the operation
     * @param executionTimeMs execution time in milliseconds
     * @param success         whether the operation was successful
     * @param context         additional contextual data
     */
    void logPerformance(String operationName, long executionTimeMs, boolean success, Map<String, Object> context);

    /**
     * Log an audit event for compliance and security tracking
     * 
     * @param action   the action performed
     * @param userId   the user performing the action
     * @param resource the resource affected
     * @param details  additional details about the action
     */
    void logAudit(String action, String userId, String resource, Map<String, Object> details);
}
