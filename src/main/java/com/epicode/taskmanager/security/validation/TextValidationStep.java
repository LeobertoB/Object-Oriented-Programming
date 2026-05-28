package com.epicode.taskmanager.security.validation;

public abstract class TextValidationStep {
    private TextValidationStep next;

    public TextValidationStep linkWith(TextValidationStep next) {
        this.next = next;
        return next;
    }

    public final void validate(TextValidationContext context) {
        handle(context);
        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void handle(TextValidationContext context);
}
