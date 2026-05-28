package com.epicode.taskmanager;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {
    @Test
    void applicationStartsWithoutErrors() {
        final ByteArrayInputStream input = new ByteArrayInputStream("0%n".formatted().getBytes(StandardCharsets.UTF_8));
        System.setIn(input);

        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
