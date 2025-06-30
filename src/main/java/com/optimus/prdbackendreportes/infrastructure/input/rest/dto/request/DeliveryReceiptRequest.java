package com.optimus.prdbackendreportes.infrastructure.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DeliveryReceiptRequest(
        @NotBlank(message = "Account cannot be blank")
        String account,

        @NotNull(message = "Process date cannot be null")
        LocalDate processDate,

        @Positive(message = "Process batch must be positive")
        int processBatch,

        String orderNumber,

        @NotBlank(message = "Schema cannot be blank")
        String schema
) {
}
