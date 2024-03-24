package com.tosDev.tr.enums;

public enum ExpenseCategory {
    PRODUCT ("product"),
    SERVICE ("service");

    private final String description;

    ExpenseCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
