package com.optimus.prdbackendreportes.utils.exceptions;

/**
 * Excepción lanzada cuando ocurre un error al generar un reporte
 */
public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}