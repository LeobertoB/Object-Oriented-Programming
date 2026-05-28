package com.epicode.taskmanager.security.validation;

import com.epicode.taskmanager.security.exception.ValidationException;

public final class MaxLengthValidationStep extends TextValidationStep {
    @Override
    protected void handle(TextValidationContext context) {
        if (context.getValue().length() > context.getMaxLength()) {
            throw new ValidationException(
                    context.getFieldName() + " cannot exceed " + context.getMaxLength() + " characters"
            );
        }
    }
}
