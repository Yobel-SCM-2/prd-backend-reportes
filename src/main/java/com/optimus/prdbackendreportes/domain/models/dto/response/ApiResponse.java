package com.optimus.prdbackendreportes.domain.models.dto.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String timestamp
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, Instant.now().toString());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, Instant.now().toString());
    }
}
