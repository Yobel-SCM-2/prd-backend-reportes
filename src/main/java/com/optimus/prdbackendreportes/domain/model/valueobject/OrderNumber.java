package com.optimus.prdbackendreportes.domain.model.valueobject;

public record OrderNumber(String value) {

    public OrderNumber {
        // OrderNumber can be optional/empty
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    public static OrderNumber empty() {
        return new OrderNumber("");
    }

    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }
}