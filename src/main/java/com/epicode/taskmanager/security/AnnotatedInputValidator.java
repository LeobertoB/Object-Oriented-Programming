package com.epicode.taskmanager.security;

import com.epicode.taskmanager.security.annotation.SanitizedText;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AnnotatedInputValidator {
    private AnnotatedInputValidator() {
    }

    public static Map<String, String> sanitize(Object input) {
        Objects.requireNonNull(input, "input cannot be null");
        Map<String, String> values = new HashMap<>();

        for (Field field : input.getClass().getDeclaredFields()) {
            SanitizedText annotation = field.getAnnotation(SanitizedText.class);
            if (annotation != null) {
                values.put(field.getName(), sanitizeField(input, field, annotation));
            }
        }

        return Map.copyOf(values);
    }

    private static String sanitizeField(Object input, Field field, SanitizedText annotation) {
        try {
            field.setAccessible(true);
            Object rawValue = field.get(input);
            String fieldName = annotation.fieldName().isBlank() ? field.getName() : annotation.fieldName();
            return InputSanitizer.sanitizeText((String) rawValue, fieldName, annotation.maxLength());
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Annotated input could not be read.");
        }
    }
}
