package com.epicode.taskmanager.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class ApplicationLoggerFactory {
    private static final String CONFIGURATION_FILE = "/logging.properties";
    private static boolean configured;

    private ApplicationLoggerFactory() {
    }

    public static Logger getLogger(Class<?> type) {
        Objects.requireNonNull(type, "type cannot be null");
        configureIfNeeded();
        return Logger.getLogger(type.getName());
    }

    private static synchronized void configureIfNeeded() {
        if (configured) {
            return;
        }

        try (InputStream inputStream = ApplicationLoggerFactory.class.getResourceAsStream(CONFIGURATION_FILE)) {
            if (inputStream != null) {
                LogManager.getLogManager().readConfiguration(inputStream);
            }
            configured = true;
        } catch (IOException exception) {
            Logger.getLogger(ApplicationLoggerFactory.class.getName())
                    .warning("Logging configuration could not be loaded.");
            configured = true;
        }
    }
}
