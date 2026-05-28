package com.epicode.taskmanager.security;

import com.epicode.taskmanager.security.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputSanitizerTest {
    @Test
    void trimsTextAndRemovesUnsafeControlCharacters() {
        final String sanitized = InputSanitizer.sanitizeText("  Valid\u0000 title\u0007  ", "title", 80);

        assertEquals("Valid title", sanitized);
    }

    @Test
    void rejectsNullTextWithValidationException() {
        assertThrows(ValidationException.class, () -> InputSanitizer.sanitizeText(null, "title", 80));
    }

    @Test
    void rejectsBlankTextWithValidationException() {
        assertThrows(ValidationException.class, () -> InputSanitizer.sanitizeText("   ", "title", 80));
    }

    @Test
    void rejectsTextLongerThanAllowedMaximum() {
        assertThrows(ValidationException.class, () -> InputSanitizer.sanitizeText("a".repeat(81), "title", 80));
    }
}
