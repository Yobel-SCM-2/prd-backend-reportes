package com.optimus.prdbackendreportes.utils.exceptions;

/**
 * Excepción lanzada cuando no se encuentran datos para generar un reporte
 */
public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String message) {
        super(message);
    }
}