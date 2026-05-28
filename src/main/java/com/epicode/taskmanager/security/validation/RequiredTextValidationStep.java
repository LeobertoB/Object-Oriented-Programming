package com.epicode.taskmanager.security.validation;

import com.epicode.taskmanager.security.exception.ValidationException;

public final class RequiredTextValidationStep extends TextValidationStep {
    @Override
    protected void handle(TextValidationContext context) {
        if (context.getValue() == null) {
            throw new ValidationException(context.getFieldName() + " is required");
        }
    }
}
