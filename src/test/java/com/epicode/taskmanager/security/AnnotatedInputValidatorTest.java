package com.epicode.taskmanager.security;

import com.epicode.taskmanager.security.annotation.SanitizedText;
import com.epicode.taskmanager.security.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnnotatedInputValidatorTest {
    @Test
    void sanitizesAnnotatedFieldsUsingReflection() {
        final Map<String, String> values = AnnotatedInputValidator.sanitize(new Input("  Safe\u0000 title  "));

        assertEquals("Safe title", values.get("title"));
    }

    @Test
    void rejectsInvalidAnnotatedFields() {
        assertThrows(ValidationException.class, () -> AnnotatedInputValidator.sanitize(new Input("    ")));
    }

    private record Input(@SanitizedText(maxLength = 20) String title) {
    }
}
