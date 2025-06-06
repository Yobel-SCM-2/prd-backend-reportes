package com.optimus.prdbackendreportes.domain.models.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InfoCabeceraRequest(
        @NotBlank(message = "Account cannot be blank")
        String account,
        @NotBlank(message = "Process date cannot be blank")
        String processDate,
        @Min(value = 1, message = "Process batch must be greater than 0")
        Integer processBatch
) {
}
