package com.optimus.prdbackendreportes.infrastructure.adapter.output.report;

import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.request.DeliveryReceiptRequest;
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


@Component
@Log4j2
public class PdfReportGenerator {

    private static final int MAX_ITEMS_PER_PAGE = 34;

    public byte[] generateDeliveryReceiptReport(List<DeliveryReceiptItem> data, DeliveryReceiptRequest request) {
        try {
            log.info("Generating PDF report for {} items", data.size());

            validateInputData(data, request);

            Map<String, Integer> totalPagesMap = calculateTotalPagesPerOrder(data);
            InputStream reportTemplate = loadReportTemplate();
            InputStream banner = loadCompanyBanner();

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
            Map<String, Object> parameters = buildReportParameters(request, data, totalPagesMap, banner);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportTemplate, parameters, dataSource);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            log.info("PDF report generated successfully, size: {} bytes", pdfBytes.length);
            return pdfBytes;

        } catch (ReportGenerationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            throw new ReportGenerationException("Error al generar reporte PDF: " + e.getMessage(), e);
        }
    }

    private void validateInputData(List<DeliveryReceiptItem> data, DeliveryReceiptRequest request) {
        if (data == null || data.isEmpty()) {
            throw new ReportGenerationException("No hay datos para generar el reporte");
        }
        if (request == null) {
            throw new ReportGenerationException("Request de reporte no puede ser nulo");
        }
    }

    private InputStream loadReportTemplate() {
        InputStream template = getClass().getResourceAsStream(DELIVERY_RECEIPT_TEMPLATE);
        if (template == null) {
            throw new ReportGenerationException("Plantilla de reporte no encontrada: " + DELIVERY_RECEIPT_TEMPLATE);
        }
        return template;
    }

    private InputStream loadCompanyBanner() {
        InputStream banner = getClass().getResourceAsStream(COMPANY_BANNER);
        if (banner == null) {
            log.warn("Banner de empresa no encontrado: {}", COMPANY_BANNER);
            return null;
        }
        return banner;
    }

    private Map<String, Object> buildReportParameters(DeliveryReceiptRequest request,
                                                      List<DeliveryReceiptItem> data,
                                                      Map<String, Integer> totalPagesMap,
                                                      InputStream banner) {
        Map<String, Object> parameters = new HashMap<>();

        // Basic report parameters
        parameters.put("banner", banner);
        parameters.put("account", request.account());
        parameters.put("processDate", java.sql.Date.valueOf(request.processDate()));
        parameters.put("processBatch", request.processBatch());
        parameters.put("orderNumber", request.orderNumber());
        parameters.put("totalPagesMap", totalPagesMap);
        parameters.put("maxItemsPerPage", MAX_ITEMS_PER_PAGE);

        // Header parameters are only added if there is at least one item in the data
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
