package com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.response;

public record FileInfoResponse(
        String fileName,
        String contentType,
        long size
) {
}
