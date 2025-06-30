package com.optimus.prdbackendreportes.infrastructure.input.rest.dto.response;

public record FileInfoResponse(
        String fileName,
        String contentType,
        long size
) {
}
