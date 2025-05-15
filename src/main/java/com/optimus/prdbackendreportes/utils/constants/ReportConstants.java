package com.optimus.prdbackendreportes.utils.constants;

/**
 * Constantes utilizadas en los reportes
 */
public class ReportConstants {
    // Rutas de plantillas de reportes
    public static final String DELIVERY_RECEIPT_TEMPLATE = "/reports/delivery_receipt/receipt.jasper";
    public static final String COMPANY_BANNER = "/reports/banner.png";

    // Mensajes de error
    public static final String NO_DATA_FOUND = "No se encontraron datos para generar el reporte";
    public static final String REPORT_GENERATION_ERROR = "Error al generar el reporte";

    private ReportConstants() {
        // Constructor privado para evitar instanciación
    }
}