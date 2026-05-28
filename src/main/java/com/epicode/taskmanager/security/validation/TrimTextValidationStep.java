package com.epicode.taskmanager.security.validation;

public final class TrimTextValidationStep extends TextValidationStep {
    @Override
    protected void handle(TextValidationContext context) {
        context.setValue(context.getValue().trim());
    }
}
