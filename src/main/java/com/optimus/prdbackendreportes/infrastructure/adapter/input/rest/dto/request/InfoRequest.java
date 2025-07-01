package com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InfoRequest(
        @NotBlank(message = "Account cannot be blank")
        String account,

        @NotBlank(message = "Process date cannot be blank")
        String processDate,

        @NotNull(message = "Process batch cannot be null")
        @Min(value = 1, message = "Process batch must be greater than 0")
        Integer processBatch
) {
}
