package com.optimus.prdbackendreportes.application.services.interfaces;

import com.optimus.prdbackendreportes.domain.models.dto.request.DeliveryReceiptRequest;

/**
 * Interfaz para el servicio de constancias de entrega
 */
public interface IDeliveryReceiptService {

    /**
     * Genera un reporte PDF de constancia de entrega
     *
     * @param request Parámetros para el reporte
     * @return Bytes del PDF generado
     */
    byte[] generateDeliveryReceiptReport(DeliveryReceiptRequest request);

    /**
     * Valida que existan datos para generar el reporte
     *
     * @param request Parámetros para validar
     * @throws RuntimeException si no existen datos
     */
    void validateReportData(DeliveryReceiptRequest request);
}