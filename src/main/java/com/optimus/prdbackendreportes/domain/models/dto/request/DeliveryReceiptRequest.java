package com.optimus.prdbackendreportes.domain.models.dto.request;

import java.time.LocalDate;

public record DeliveryReceiptRequest(
        String account,
        LocalDate processDate,
        int processBatch,
        String orderNumber,
        String schema
) {
}
