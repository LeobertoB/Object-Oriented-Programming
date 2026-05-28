package com.epicode.taskmanager.logging;

import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationLoggerFactoryTest {
    @Test
    void createsLoggerForRequestedClass() {
        final Logger logger = ApplicationLoggerFactory.getLogger(ApplicationLoggerFactoryTest.class);

        assertNotNull(logger);
        assertEquals(ApplicationLoggerFactoryTest.class.getName(), logger.getName());
    }
}
