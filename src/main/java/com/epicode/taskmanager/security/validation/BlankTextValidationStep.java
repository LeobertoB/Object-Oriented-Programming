package com.epicode.taskmanager.security.validation;

import com.epicode.taskmanager.security.exception.ValidationException;

public final class BlankTextValidationStep extends TextValidationStep {
    @Override
    protected void handle(TextValidationContext context) {
        if (context.getValue().isEmpty()) {
            throw new ValidationException(context.getFieldName() + " cannot be blank");
        }
    }
}
