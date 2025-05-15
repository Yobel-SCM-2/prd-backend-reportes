package com.optimus.prdbackendreportes.domain.repositories;

import com.optimus.prdbackendreportes.domain.models.dto.response.DeliveryReceiptItem;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto principal para el repositorio de constancias de entrega.
 * Define las operaciones que cualquier implementación debe proveer.
 */
public interface IDeliveryReceiptRepository {
    /**
     * Obtiene los datos para el reporte de constancia de entrega
     *
     * @param account      Código de cuenta
     * @param processDate  Fecha de proceso
     * @param processBatch Lote de proceso
     * @param orderNumber  Número de orden (opcional)
     * @param schema       Esquema de la base de datos
     * @return Lista de ítems para el reporte
     */
    List<DeliveryReceiptItem> getDeliveryReceiptData(
            String account,
            LocalDate processDate,
            int processBatch,
            String orderNumber,
            String schema
    );

    /**
     * Valida si existen datos para generar el reporte
     *
     * @param account      Código de cuenta
     * @param processDate  Fecha de proceso
     * @param processBatch Lote de proceso
     * @param orderNumber  Número de orden (opcional)
     * @param schema       Esquema de la base de datos
     * @return Cantidad de registros encontrados
     */
    int validateDataExists(
            String account,
            LocalDate processDate,
            int processBatch,
            String orderNumber,
            String schema
    );
}