package com.optimus.prdbackendreportes.utils.exceptions;

import com.optimus.prdbackendreportes.domain.models.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador de excepciones para reportes
 */
@RestControllerAdvice
public class DeliveryReceiptExceptionHandler {

    /**
     * Maneja excepciones cuando no se encuentran datos
     */
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoDataFoundException(NoDataFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                false,
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja excepciones durante la generación de reportes
     */
    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<ErrorResponse> handleReportGenerationException(ReportGenerationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                false,
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Maneja excepciones genéricas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                false,
                "Ha ocurrido un error inesperado: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}