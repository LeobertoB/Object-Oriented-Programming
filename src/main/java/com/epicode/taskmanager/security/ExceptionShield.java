package com.epicode.taskmanager.security;

import com.epicode.taskmanager.logging.ApplicationLoggerFactory;
import com.epicode.taskmanager.security.exception.ApplicationException;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExceptionShield {
    private static final String GENERIC_ERROR_MESSAGE = "The operation could not be completed safely.";

    private final Logger logger;

    public ExceptionShield() {
        this(ApplicationLoggerFactory.getLogger(ExceptionShield.class));
    }

    public ExceptionShield(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
    }

    public <T> OperationResult<T> execute(Supplier<T> operation) {
        Objects.requireNonNull(operation, "operation cannot be null");
        try {
            return OperationResult.success(operation.get());
        } catch (ApplicationException exception) {
            logger.log(Level.INFO, exception.getMessage());
            return OperationResult.failure(exception.getMessage());
        } catch (RuntimeException exception) {
            logger.log(Level.WARNING, "Unexpected application error: {0}", exception.getClass().getSimpleName());
            return OperationResult.failure(GENERIC_ERROR_MESSAGE);
        }
    }
}
