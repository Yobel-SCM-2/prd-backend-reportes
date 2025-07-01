package com.optimus.prdbackendreportes.domain.model.constants;

public class ReportConstants {

    // Report paths
    public static final String DELIVERY_RECEIPT_TEMPLATE = "/reports/delivery_receipt/receipt.jasper";
    public static final String COMPANY_BANNER = "/reports/banner.png";

    // Error messages
    public static final String NO_DATA_FOUND = "Was not found data to generate the report";
    public static final String REPORT_GENERATION_ERROR = "Error generating report";

    public static final String PACKAGE_NAME = "PKG_PRD_REPORTES";

    // Excel report headers
    public static final String[] HEADER_HEADERS = {
            "Código de cuenta", "Fecha de Proceso", "Lote de Proceso", "Zona de Consultora",
            "Fecha de Emisión", "Número de Boleta", "Secuencia de Boleta", "Código de Consultora",
            "Nombre de Consultora", "Total de Unidades", "Número de Pedido Relacionado",
            "Secuencia de Pedido Relacionado"
    };

    public static final String[] DETAIL_HEADERS = {
            "Código de cuenta", "Fecha de Proceso", "Lote de Proceso", "Zona de Consultora",
            "Fecha de Emisión", "Número de Boleta", "Secuencia de Boleta", "Código de Consultora",
            "Nombre de Consultora", "Total de Unidades", "Código de Transacción Pedido Relacionado",
            "Número de Pedido Relacionado", "Secuencia de Pedido Relacionado", "Código de Producto",
            "Descripción de Producto", "Unidades a Recoger", "Campaña de Atención", "Tipo de Atención"
    };

    private ReportConstants() {
        // Private constructor to prevent instantiation
    }
}
