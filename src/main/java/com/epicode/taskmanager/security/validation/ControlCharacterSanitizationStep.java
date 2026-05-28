package com.epicode.taskmanager.security.validation;

public final class ControlCharacterSanitizationStep extends TextValidationStep {
    @Override
    protected void handle(TextValidationContext context) {
        context.setValue(context.getValue().replaceAll("[\\p{Cntrl}&&[^\n\t]]", ""));
    }
}
