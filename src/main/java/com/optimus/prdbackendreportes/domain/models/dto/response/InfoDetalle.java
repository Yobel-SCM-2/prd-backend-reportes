package com.optimus.prdbackendreportes.domain.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoDetalle {
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
    private String relatedOrderTransactionCode;
    private String relatedOrderNumber;
    private Integer relatedOrderSequence;
    private String productCode;
    private String productDescription;
    private Integer unitsToBePicked;
    private String attentionCampaign;
    private String attentionType;
}
