package com.optimus.prdbackendreportes.infrastructure.input.rest.dto.response;

public record Response<T>(
        boolean success,
        String message,
        T data
) {
}
