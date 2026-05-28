package com.epicode.taskmanager.security.exception;

public final class PersistenceException extends ApplicationException {
    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
