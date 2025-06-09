package com.optimus.prdbackendreportes.domain.models.dto.response;

public record FileInfoResponse(
        String fileName,
        String contentType,
        long size
) {
}
