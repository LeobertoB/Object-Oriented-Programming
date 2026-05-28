package com.epicode.taskmanager.security;

import com.epicode.taskmanager.security.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionShieldTest {
    private final TestLogHandler logHandler = new TestLogHandler();
    private final Logger logger = testLogger();
    private final ExceptionShield shield = new ExceptionShield(logger);

    @Test
    void returnsSuccessResultWhenOperationSucceeds() {
        final OperationResult<String> result = shield.execute(() -> "created");

        assertTrue(result.isSuccess());
        assertEquals("created", result.getValue().orElseThrow());
        assertEquals("Operation completed successfully.", result.getMessage());
    }

    @Test
    void returnsValidationMessageForExpectedApplicationException() {
        final OperationResult<String> result = shield.execute(() -> {
            throw new ValidationException("title cannot be blank");
        });

        assertFalse(result.isSuccess());
        assertEquals("title cannot be blank", result.getMessage());
        assertTrue(result.getValue().isEmpty());
    }

    @Test
    void hidesUnexpectedExceptionDetailsFromUserFacingResult() {
        final OperationResult<String> result = shield.execute(() -> {
            throw new IllegalStateException("database path /private/secret failed");
        });

        assertFalse(result.isSuccess());
        assertEquals("The operation could not be completed safely.", result.getMessage());
        assertTrue(result.getValue().isEmpty());
    }

    @Test
    void logsUnexpectedErrorsWithoutThrowableStackTrace() {
        shield.execute(() -> {
            throw new IllegalStateException("secret-token-123");
        });

        final LogRecord record = logHandler.records().get(0);
        assertEquals(Level.WARNING, record.getLevel());
        assertEquals("Unexpected application error: {0}", record.getMessage());
        assertEquals("IllegalStateException", record.getParameters()[0]);
        assertEquals(null, record.getThrown());
    }

    private Logger testLogger() {
        final Logger testLogger = Logger.getLogger(ExceptionShieldTest.class.getName() + "." + System.nanoTime());
        testLogger.setUseParentHandlers(false);
        testLogger.addHandler(logHandler);
        return testLogger;
    }

    private static final class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        List<LogRecord> records() {
            return records;
        }
    }
}
