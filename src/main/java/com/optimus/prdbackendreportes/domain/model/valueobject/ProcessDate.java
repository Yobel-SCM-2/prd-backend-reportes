package com.optimus.prdbackendreportes.domain.model.valueobject;

import java.time.LocalDate;
import java.util.Objects;

public record ProcessDate(LocalDate value) {

    public ProcessDate {
        Objects.requireNonNull(value, "Process date cannot be null");
    }

    public static ProcessDate of(LocalDate date) {
        return new ProcessDate(date);
    }

    public static ProcessDate of(String dateString) {
        return new ProcessDate(LocalDate.parse(dateString));
    }
}