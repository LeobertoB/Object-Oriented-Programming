package com.epicode.taskmanager.security.validation;

public final class TextValidationContext {
    private final String fieldName;
    private final int maxLength;
    private String value;

    public TextValidationContext(String value, String fieldName, int maxLength) {
        this.value = value;
        this.fieldName = fieldName;
        this.maxLength = maxLength;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
