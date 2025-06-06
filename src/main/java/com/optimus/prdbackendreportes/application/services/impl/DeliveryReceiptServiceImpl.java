package com.optimus.prdbackendreportes.application.services.impl;

import com.optimus.prdbackendreportes.application.services.interfaces.IDeliveryReceiptService;
import com.optimus.prdbackendreportes.domain.models.dto.request.DeliveryReceiptRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.repositories.IDeliveryReceiptRepository;
import com.optimus.prdbackendreportes.utils.exceptions.NoDataFoundException;
import com.optimus.prdbackendreportes.utils.exceptions.ReportGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.optimus.prdbackendreportes.utils.constants.ReportConstants.*;

/**
 * Implementación del servicio para generar reportes de constancia de entrega
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class DeliveryReceiptServiceImpl implements IDeliveryReceiptService {

    private final IDeliveryReceiptRepository repository;
    private static final int MAX_ITEMS_PER_PAGE = 34;

    @Override
    public byte[] generateDeliveryReceiptReport(DeliveryReceiptRequest request) {
        try {
            // Obtener datos del repositorio
            List<DeliveryReceiptItem> reportData = repository.getDeliveryReceiptData(
                    request.account(),
                    request.processDate(),
                    request.processBatch(),
                    request.orderNumber(),
                    request.schema()
            );

            if (reportData.isEmpty()) {
                throw new NoDataFoundException(NO_DATA_FOUND);
            }

            // Precalcular la cantidad de páginas necesarias para cada orden
            Map<String, Integer> totalPagesMap = calculateTotalPagesPerOrder(reportData);

            // Log para depuración
            totalPagesMap.forEach((order, pages) ->
                    log.info("Orden: {}, Páginas calculadas: {}", order, pages));

            // Cargar template del reporte
            InputStream reportTemplate = getResource(DELIVERY_RECEIPT_TEMPLATE);
            InputStream banner = getResource(COMPANY_BANNER);

            // Crear fuente de datos para JasperReports
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);

            // Configurar parámetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("banner", banner);
            parameters.put("account", request.account());
            parameters.put("processDate", java.sql.Date.valueOf(request.processDate()));
            parameters.put("processBatch", request.processBatch());
            parameters.put("orderNumber", request.orderNumber());
            parameters.put("totalPagesMap", totalPagesMap);
            parameters.put("maxItemsPerPage", MAX_ITEMS_PER_PAGE);

            // Agregar parámetros de cabecera desde el primer elemento del reporte
            if (!reportData.isEmpty()) {
                DeliveryReceiptItem firstItem = reportData.getFirst();
                parameters.put("customerNameParam", firstItem.getCustomerName());
                parameters.put("destinationCodeParam", firstItem.getDestinationCode());
                parameters.put("deliveryAddressParam", firstItem.getDeliveryAddress());
                parameters.put("addressReferenceParam", firstItem.getAddressReference());
                parameters.put("phoneNumberParam", firstItem.getPhoneNumber());
                parameters.put("registrationDateParam", firstItem.getRegistrationDate().toLocalDate());
                parameters.put("dispatchDateParam", firstItem.getDispatchDate().toLocalDate());
                parameters.put("orderTypeParam", firstItem.getOrderType());
            }

            // Generar reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportTemplate, parameters, dataSource);

            // Convertir a PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al generar reporte de constancia de entrega", e);
            throw new ReportGenerationException(REPORT_GENERATION_ERROR + e.getMessage());
        }
    }

    /**
     * Calcula el número total de páginas necesarias para cada orden
     * basado en la cantidad de productos y el número máximo de productos por página
     */
    private Map<String, Integer> calculateTotalPagesPerOrder(List<DeliveryReceiptItem> items) {
        // Agrupar items por número de orden
        Map<String, List<DeliveryReceiptItem>> itemsByOrder = items.stream()
                .collect(Collectors.groupingBy(DeliveryReceiptItem::getOrderNumber));

        // Calcular páginas para cada orden
        Map<String, Integer> totalPagesMap = new HashMap<>();

        for (Map.Entry<String, List<DeliveryReceiptItem>> entry : itemsByOrder.entrySet()) {
            String orderNumber = entry.getKey();
            int itemCount = entry.getValue().size();
            int totalPages = (int) Math.ceil((double) itemCount / MAX_ITEMS_PER_PAGE);
            totalPagesMap.put(orderNumber, Math.max(1, totalPages)); // Mínimo 1 página

            // Log para depuración
            log.info("Orden: {}, Items: {}, Páginas calculadas: {}",
                    orderNumber, itemCount, totalPages);
        }

        return totalPagesMap;
    }

    @Override
    public void validateReportData(DeliveryReceiptRequest request) {
        int dataCount = repository.validateDataExists(
                request.account(),
                request.processDate(),
                request.processBatch(),
                request.orderNumber(),
                request.schema()
        );

        if (dataCount == 0) {
            throw new NoDataFoundException("No existen datos para generar el reporte con los parámetros proporcionados");
        }
    }

    /**
     * Obtiene un recurso como InputStream
     */
    private InputStream getResource(String path) {
        return this.getClass().getResourceAsStream(path);
    }
}