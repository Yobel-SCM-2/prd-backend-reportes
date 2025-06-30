package com.optimus.prdbackendreportes.domain.model.valueobject;

public record Account(String value) {

    public Account {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Account cannot be null or empty");
        }
    }

    public static Account of(String value) {
        return new Account(value);
    }
}