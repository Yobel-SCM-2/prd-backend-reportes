package com.optimus.prdbackendreportes.domain.port.output;

import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.enums.ReportFormat;

import java.util.List;

public interface ReportGenerator {


    /**
     * Generates a delivery receipt report
     */
    byte[] generateDeliveryReceiptReport(List<DeliveryReceiptItem> data,
                                         Object request,
                                         ReportFormat format);

    /**
     * Generates a report containing information of the header
     */
    byte[] generateHeaderInfoReport(List<HeaderInfo> data,
                                    Object request,
                                    ReportFormat format);

    /**
     * Generates a report containing information of the detail
     */
    byte[] generateDetailInfoReport(List<DetailInfo> data,
                                    Object request,
                                    ReportFormat format);

    /**
     * Verify if the generator supports the specified report format.
     */
    boolean supports(ReportFormat format);

    /**
     * Get the supported report format by this generator.
     */
    ReportFormat getSupportedFormat();
}
