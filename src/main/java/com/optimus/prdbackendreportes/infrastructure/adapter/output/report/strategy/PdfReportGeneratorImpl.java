package com.optimus.prdbackendreportes.infrastructure.adapter.output.report.strategy;

import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.enums.ReportFormat;
import com.optimus.prdbackendreportes.domain.port.output.ReportGenerator;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.request.DeliveryReceiptRequest;
import com.optimus.prdbackendreportes.infrastructure.util.ResourceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.optimus.prdbackendreportes.domain.model.constants.ReportConstants.COMPANY_BANNER;
import static com.optimus.prdbackendreportes.domain.model.constants.ReportConstants.DELIVERY_RECEIPT_TEMPLATE;

/**
 * Strategy Implementation for generating reports in PDF format
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class PdfReportGeneratorImpl implements ReportGenerator {

    private static final int MAX_ITEMS_PER_PAGE = 34;

    private final ResourceManager resourceManager;

    @Override
    public byte[] generateDeliveryReceiptReport(List<DeliveryReceiptItem> data,
                                                Object request,
                                                ReportFormat format) {
        validateFormat(format);
        DeliveryReceiptRequest deliveryRequest = castRequest(request, DeliveryReceiptRequest.class);

        try {
            log.info("Generating PDF delivery receipt report for {} items", data.size());
            validateInputData(data, deliveryRequest);

            Map<String, Integer> totalPagesMap = calculateTotalPagesPerOrder(data);

            try (InputStream reportTemplate = resourceManager.getResourceAsStream(DELIVERY_RECEIPT_TEMPLATE);
                 InputStream banner = resourceManager.getResourceAsStream(COMPANY_BANNER)) {

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
                Map<String, Object> parameters = buildDeliveryReceiptParameters(
                        deliveryRequest, data, totalPagesMap, banner);

                JasperPrint jasperPrint = JasperFillManager.fillReport(reportTemplate, parameters, dataSource);
                byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

                log.info("PDF delivery receipt report generated successfully, size: {} bytes", pdfBytes.length);
                return pdfBytes;
            }
        } catch (ReportGenerationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating PDF delivery receipt report", e);
            throw new ReportGenerationException("Error al generar reporte PDF de constancia de entrega: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateHeaderInfoReport(List<HeaderInfo> data, Object request, ReportFormat format) {
        throw new UnsupportedOperationException("PDF format not supported for HeaderInfo reports. Use EXCEL format instead.");
    }

    @Override
    public byte[] generateDetailInfoReport(List<DetailInfo> data, Object request, ReportFormat format) {
        throw new UnsupportedOperationException("PDF format not supported for DetailInfo reports. Use EXCEL format instead.");
    }

    @Override
    public boolean supports(ReportFormat format) {
        return ReportFormat.PDF.equals(format);
    }

    @Override
    public ReportFormat getSupportedFormat() {
        return ReportFormat.PDF;
    }

    // Auxiliary private methods
    private void validateFormat(ReportFormat format) {
        if (!supports(format)) {
            throw new ReportGenerationException("Not supported format: " + format + ". This generator only supports PDF.");
        }
    }

    private <T> T castRequest(Object request, Class<T> expectedType) {
        if (!expectedType.isInstance(request)) {
            throw new ReportGenerationException(
                    "Incorrect type of request. Was expected " + expectedType.getSimpleName() +
                            " but was received " + request.getClass().getSimpleName()
            );
        }
        return expectedType.cast(request);
    }

    private void validateInputData(List<?> data, Object request) {
        if (data == null || data.isEmpty()) {
            throw new ReportGenerationException("There is not data to generate the PDF report");
        }
        if (request == null) {
            throw new ReportGenerationException("Request object cannot be null");
        }
    }

    private Map<String, Object> buildDeliveryReceiptParameters(DeliveryReceiptRequest request,
                                                               List<DeliveryReceiptItem> data,
                                                               Map<String, Integer> totalPagesMap,
                                                               InputStream banner) {
        Map<String, Object> parameters = new HashMap<>();

        // Report basic parameters
        parameters.put("banner", banner);
        parameters.put("account", request.account());
        parameters.put("processDate", java.sql.Date.valueOf(request.processDate()));
        parameters.put("processBatch", request.processBatch());
        parameters.put("orderNumber", request.orderNumber());
        parameters.put("totalPagesMap", totalPagesMap);
        parameters.put("maxItemsPerPage", MAX_ITEMS_PER_PAGE);

        // First element's header parameters
        if (!data.isEmpty()) {
            addHeaderParameters(parameters, data.getFirst());
        }

        return parameters;
    }

    private void addHeaderParameters(Map<String, Object> parameters, DeliveryReceiptItem firstItem) {
        parameters.put("customerNameParam", firstItem.getCustomerName());
        parameters.put("destinationCodeParam", firstItem.getDestinationCode());
        parameters.put("deliveryAddressParam", firstItem.getDeliveryAddress());
        parameters.put("addressReferenceParam", firstItem.getAddressReference());
        parameters.put("phoneNumberParam", firstItem.getPhoneNumber());

        if (firstItem.getRegistrationDate() != null) {
            parameters.put("registrationDateParam", firstItem.getRegistrationDate().toLocalDate());
        }
        if (firstItem.getDispatchDate() != null) {
            parameters.put("dispatchDateParam", firstItem.getDispatchDate().toLocalDate());
        }

        parameters.put("orderTypeParam", firstItem.getOrderType());
    }

    private Map<String, Integer> calculateTotalPagesPerOrder(List<DeliveryReceiptItem> items) {
        log.debug("Calculating total pages for {} items", items.size());

        Map<String, List<DeliveryReceiptItem>> itemsByOrder = items.stream()
                .collect(Collectors.groupingBy(DeliveryReceiptItem::getOrderNumber));

        Map<String, Integer> totalPagesMap = new HashMap<>();

        for (Map.Entry<String, List<DeliveryReceiptItem>> entry : itemsByOrder.entrySet()) {
            String orderNumber = entry.getKey();
            int itemCount = entry.getValue().size();
            int totalPages = (int) Math.ceil((double) itemCount / MAX_ITEMS_PER_PAGE);
            int finalPages = Math.max(1, totalPages);

            totalPagesMap.put(orderNumber, finalPages);
            log.debug("Order: {}, Items: {}, Pages: {}", orderNumber, itemCount, finalPages);
        }

        return totalPagesMap;
    }
}
