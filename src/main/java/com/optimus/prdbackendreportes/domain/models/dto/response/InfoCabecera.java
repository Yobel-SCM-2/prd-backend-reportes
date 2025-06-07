package com.optimus.prdbackendreportes.domain.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoCabecera {
    private String account;
    private String processDate;
    private Integer processBatch;
    private String consultantArea;
    private String issueDate;
    private String ticketNumber;
    private String ticketSequence;
    private String consultantCode;
    private String consultantName;
    private Integer totalUnits;
    private String relatedOrderNumber;
    private Integer relatedOrderSequence;
}
