package com.optimus.prdbackendreportes.domain.model.valueobject;

public record ProcessBatch(int value) {

    public ProcessBatch {
        if (value <= 0) {
            throw new IllegalArgumentException("Process batch must be greater than 0");
        }
    }

    public static ProcessBatch of(int value) {
        return new ProcessBatch(value);
    }
}