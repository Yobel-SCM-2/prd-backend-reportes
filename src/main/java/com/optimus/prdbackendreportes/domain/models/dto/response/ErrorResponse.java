package com.optimus.prdbackendreportes.domain.models.dto.response;

public record ErrorResponse(
        boolean success,
        String message,
        String error
) {
}
