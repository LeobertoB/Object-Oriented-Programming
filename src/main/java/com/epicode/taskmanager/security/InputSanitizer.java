package com.epicode.taskmanager.security;

import com.epicode.taskmanager.security.validation.BlankTextValidationStep;
import com.epicode.taskmanager.security.validation.ControlCharacterSanitizationStep;
import com.epicode.taskmanager.security.validation.MaxLengthValidationStep;
import com.epicode.taskmanager.security.validation.RequiredTextValidationStep;
import com.epicode.taskmanager.security.validation.TextValidationContext;
import com.epicode.taskmanager.security.validation.TextValidationStep;
import com.epicode.taskmanager.security.validation.TrimTextValidationStep;

import java.util.Objects;

public final class InputSanitizer {
    private InputSanitizer() {
    }

    public static String sanitizeText(String value, String fieldName, int maxLength) {
        Objects.requireNonNull(fieldName, "fieldName cannot be null");
        TextValidationContext context = new TextValidationContext(value, fieldName, maxLength);
        validationChain().validate(context);
        return context.getValue();
    }

    private static TextValidationStep validationChain() {
        TextValidationStep required = new RequiredTextValidationStep();
        required
                .linkWith(new ControlCharacterSanitizationStep())
                .linkWith(new TrimTextValidationStep())
                .linkWith(new BlankTextValidationStep())
                .linkWith(new MaxLengthValidationStep());
        return required;
    }
}
