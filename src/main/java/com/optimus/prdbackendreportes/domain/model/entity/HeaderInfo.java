package com.optimus.prdbackendreportes.domain.model.entity;

public class HeaderInfo {
    private final String account;
    private final String processDate;
    private final Integer processBatch;
    private final String consultantArea;
    private final String issueDate;
    private final String ticketNumber;
    private final String ticketSequence;
    private final String consultantCode;
    private final String consultantName;
    private final Integer totalUnits;
    private final String relatedOrderNumber;
    private final Integer relatedOrderSequence;

    public HeaderInfo(String account, String processDate, Integer processBatch, String consultantArea, String issueDate, String ticketNumber, String ticketSequence, String consultantCode, String consultantName, Integer totalUnits, String relatedOrderNumber, Integer relatedOrderSequence) {
        this.account = account;
        this.processDate = processDate;
        this.processBatch = processBatch;
        this.consultantArea = consultantArea;
        this.issueDate = issueDate;
        this.ticketNumber = ticketNumber;
        this.ticketSequence = ticketSequence;
        this.consultantCode = consultantCode;
        this.consultantName = consultantName;
        this.totalUnits = totalUnits;
        this.relatedOrderNumber = relatedOrderNumber;
        this.relatedOrderSequence = relatedOrderSequence;
    }

    public String getAccount() {
        return account;
    }

    public String getProcessDate() {
        return processDate;
    }

    public Integer getProcessBatch() {
        return processBatch;
    }

    public String getConsultantArea() {
        return consultantArea;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getTicketSequence() {
        return ticketSequence;
    }

    public String getConsultantCode() {
        return consultantCode;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public String getRelatedOrderNumber() {
        return relatedOrderNumber;
    }

    public Integer getRelatedOrderSequence() {
        return relatedOrderSequence;
    }
}
