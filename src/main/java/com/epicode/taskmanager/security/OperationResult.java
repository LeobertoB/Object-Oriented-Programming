package com.epicode.taskmanager.security;

import java.util.Optional;

public final class OperationResult<T> {
    private final T value;
    private final String message;
    private final boolean success;

    private OperationResult(T value, String message, boolean success) {
        this.value = value;
        this.message = message;
        this.success = success;
    }

    public static <T> OperationResult<T> success(T value) {
        return new OperationResult<>(value, "Operation completed successfully.", true);
    }

    public static <T> OperationResult<T> failure(String message) {
        return new OperationResult<>(null, message, false);
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
