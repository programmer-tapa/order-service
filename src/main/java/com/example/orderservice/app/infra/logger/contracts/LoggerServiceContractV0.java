package com.example.orderservice.app.infra.logger.contracts;

import com.example.orderservice.app.infra.logger.interfaces.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default production-grade implementation of LoggerService.
 * Uses SLF4J with Logback for flexible logging configuration.
 * Supports structured logging, correlation IDs, and contextual information.
 */
@Service
public class LoggerServiceContractV0 implements LoggerService {

    private static final Logger logger = LoggerFactory.getLogger(LoggerServiceContractV0.class);
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String OPERATION_KEY = "operation";
    private static final String EXECUTION_TIME_KEY = "executionTimeMs";
    private static final String SUCCESS_KEY = "success";
    private static final String AUDIT_ACTION_KEY = "action";
    private static final String AUDIT_USER_KEY = "userId";
    private static final String AUDIT_RESOURCE_KEY = "resource";

    private final Map<String, Object> threadLocalContext = new ConcurrentHashMap<>();

    @Override
    public void debug(String message) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.debug(message));
    }

    @Override
    public void debug(String message, Map<String, Object> context) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.debug(message, buildContextInfo(context)));
    }

    @Override
    public void info(String message) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.info(message));
    }

    @Override
    public void info(String message, Map<String, Object> context) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.info(message, buildContextInfo(context)));
    }

    @Override
    public void warn(String message) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.warn(message));
    }

    @Override
    public void warn(String message, Map<String, Object> context) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.warn(message, buildContextInfo(context)));
    }

    @Override
    public void error(String message) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.error(message));
    }

    @Override
    public void error(String message, Throwable exception) {
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");
        enrichAndLog(() -> logger.error(message, exception));
    }

    @Override
    public void error(String message, Throwable exception, Map<String, Object> context) {
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");
        enrichAndLog(() -> logger.error(message + " | " + buildContextInfo(context), exception));
    }

    @Override
    public void critical(String message) {
        Objects.requireNonNull(message, "Message cannot be null");
        enrichAndLog(() -> logger.error("[CRITICAL] " + message));
    }

    @Override
    public void critical(String message, Throwable exception) {
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");
        enrichAndLog(() -> logger.error("[CRITICAL] " + message, exception));
    }

    @Override
    public void critical(String message, Throwable exception, Map<String, Object> context) {
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");
        enrichAndLog(() -> logger.error("[CRITICAL] " + message + " | " + buildContextInfo(context), exception));
    }

    @Override
    public void setCorrelationId(String correlationId) {
        Objects.requireNonNull(correlationId, "Correlation ID cannot be null");
        MDC.put(CORRELATION_ID_KEY, correlationId);
        threadLocalContext.put(CORRELATION_ID_KEY, correlationId);
    }

    @Override
    public String getCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null) {
            correlationId = (String) threadLocalContext.get(CORRELATION_ID_KEY);
        }
        return correlationId;
    }

    @Override
    public void addContext(String key, Object value) {
        Objects.requireNonNull(key, "Context key cannot be null");
        threadLocalContext.put(key, value);
        if (value != null) {
            MDC.put(key, value.toString());
        }
    }

    @Override
    public void removeContext(String key) {
        Objects.requireNonNull(key, "Context key cannot be null");
        threadLocalContext.remove(key);
        MDC.remove(key);
    }

    @Override
    public void clearContext() {
        threadLocalContext.clear();
        MDC.clear();
    }

    @Override
    public void logPerformance(String operationName, long executionTimeMs, boolean success) {
        logPerformance(operationName, executionTimeMs, success, null);
    }

    @Override
    public void logPerformance(String operationName, long executionTimeMs, boolean success,
            Map<String, Object> context) {
        Objects.requireNonNull(operationName, "Operation name cannot be null");
        if (executionTimeMs < 0) {
            throw new IllegalArgumentException("Execution time cannot be negative");
        }

        Map<String, Object> performanceContext = new HashMap<>();
        performanceContext.put(OPERATION_KEY, operationName);
        performanceContext.put(EXECUTION_TIME_KEY, executionTimeMs);
        performanceContext.put(SUCCESS_KEY, success);
        performanceContext.put(TIMESTAMP_KEY, Instant.now().toString());

        if (context != null) {
            performanceContext.putAll(context);
        }

        String logLevel = determinePerformanceLogLevel(executionTimeMs, success);
        String message = String.format("Performance: %s - %dms - %s", operationName, executionTimeMs,
                success ? "SUCCESS" : "FAILED");

        enrichAndLog(() -> {
            if (logLevel.equals("WARN")) {
                logger.warn(message + " | " + buildContextInfo(performanceContext));
            } else if (logLevel.equals("ERROR")) {
                logger.error(message + " | " + buildContextInfo(performanceContext));
            } else {
                logger.info(message + " | " + buildContextInfo(performanceContext));
            }
        });
    }

    @Override
    public void logAudit(String action, String userId, String resource, Map<String, Object> details) {
        Objects.requireNonNull(action, "Action cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(resource, "Resource cannot be null");

        Map<String, Object> auditContext = new HashMap<>();
        auditContext.put(AUDIT_ACTION_KEY, action);
        auditContext.put(AUDIT_USER_KEY, userId);
        auditContext.put(AUDIT_RESOURCE_KEY, resource);
        auditContext.put(TIMESTAMP_KEY, Instant.now().toString());

        if (details != null) {
            auditContext.putAll(details);
        }

        String message = String.format("AUDIT: User '%s' performed '%s' on '%s'", userId, action, resource);
        enrichAndLog(() -> logger.info(message + " | " + buildContextInfo(auditContext)));
    }

    /**
     * Enriches logging with correlation ID from MDC and executes the log operation.
     */
    private void enrichAndLog(Runnable logOperation) {
        try {
            logOperation.run();
        } finally {
            // Ensure MDC is properly managed
            if (MDC.get(CORRELATION_ID_KEY) == null && threadLocalContext.get(CORRELATION_ID_KEY) != null) {
                MDC.put(CORRELATION_ID_KEY, threadLocalContext.get(CORRELATION_ID_KEY).toString());
            }
        }
    }

    /**
     * Builds a formatted context information string from a map.
     */
    private String buildContextInfo(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        context.forEach((key, value) -> {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(key).append(": ");
            if (value instanceof String) {
                sb.append("'").append(value).append("'");
            } else {
                sb.append(value);
            }
        });
        sb.append("}");
        return sb.toString();
    }

    /**
     * Determines the appropriate log level based on execution time and success
     * status.
     */
    private String determinePerformanceLogLevel(long executionTimeMs, boolean success) {
        if (!success) {
            return "ERROR";
        }
        // Log as warning if execution time exceeds 5 seconds
        if (executionTimeMs > 5000) {
            return "WARN";
        }
        return "INFO";
    }
}
