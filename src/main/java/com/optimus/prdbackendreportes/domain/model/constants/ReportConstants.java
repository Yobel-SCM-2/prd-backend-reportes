package com.optimus.prdbackendreportes.domain.model.constants;

public class ReportConstants {

    // Report paths
    public static final String DELIVERY_RECEIPT_TEMPLATE = "/reports/delivery_receipt/receipt.jasper";
    public static final String COMPANY_BANNER = "/reports/banner.png";

    // Error messages
    public static final String NO_DATA_FOUND = "Was not found data to generate the report";
    public static final String REPORT_GENERATION_ERROR = "Error generating report";

    public static final String PACKAGE_NAME = "PKG_PRD_REPORTES";

    private ReportConstants() {
        // Private constructor to prevent instantiation
    }
}
