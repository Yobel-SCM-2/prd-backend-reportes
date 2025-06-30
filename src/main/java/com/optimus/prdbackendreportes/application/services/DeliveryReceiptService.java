package com.optimus.prdbackendreportes.application.services;

import com.optimus.prdbackendreportes.application.port.input.DeliveryReceiptUseCase;
import com.optimus.prdbackendreportes.domain.exception.NoDataFoundException;
import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.OrderNumber;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;
import com.optimus.prdbackendreportes.domain.port.output.DeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class DeliveryReceiptService implements DeliveryReceiptUseCase {

    private final DeliveryReceiptRepository repository;

    @Override
    public List<DeliveryReceiptItem> generateReportData(Account account, ProcessDate processDate,
                                                        ProcessBatch processBatch, OrderNumber orderNumber, String schema) {
        try {
            log.info("Generating delivery receipt data for account: {}, date: {}, batch: {}",
                    account.value(), processDate.value(), processBatch.value());

            List<DeliveryReceiptItem> items = repository.findDeliveryReceiptData(
                    account, processDate, processBatch, orderNumber, schema);

            if (items.isEmpty()) {
                throw new NoDataFoundException("No se encontraron datos para generar el reporte");
            }

            log.info("Found {} items for delivery receipt", items.size());
            return items;

        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating delivery receipt data", e);
            throw new ReportGenerationException("Error al obtener datos del reporte: " + e.getMessage(), e);
        }
    }

    @Override
    public void validateReportData(Account account, ProcessDate processDate,
                                   ProcessBatch processBatch, OrderNumber orderNumber, String schema) {

        log.info("Validating delivery receipt data for account: {}, date: {}, batch: {}",
                account.value(), processDate.value(), processBatch.value());

        boolean exists = repository.existsDataFor(account, processDate, processBatch, orderNumber, schema);

        if (!exists) {
            throw new NoDataFoundException("No existen datos para generar el reporte con los parámetros proporcionados");
        }

        log.info("Validation successful - data exists");
    }
}
