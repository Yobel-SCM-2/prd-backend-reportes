package com.optimus.prdbackendreportes.utils.exceptions;

import com.optimus.prdbackendreportes.domain.models.dto.response.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Manejador de excepciones para reportes
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones cuando no se encuentran datos
     */
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoDataFound(NoDataFoundException ex) {
        log.warn("No data found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Maneja excepciones durante la generación de reportes
     */
    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<ApiResponse<Void>> handleReportGeneration(ReportGenerationException ex) {
        log.error("Error generating report: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al generar el reporte: " + ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Invalid parameter type: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Parámetro inválido: " + ex.getParameter().getParameterName()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Argumento inválido: " + ex.getMessage()));
    }

    /**
     * Maneja excepciones genéricas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
    }
}