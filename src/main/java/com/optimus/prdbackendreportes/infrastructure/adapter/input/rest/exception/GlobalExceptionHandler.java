package com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.exception;

import com.optimus.prdbackendreportes.domain.exception.NoDataFoundException;
import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.response.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * Handles exceptions when no data is found.
     */
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoDataFound(NoDataFoundException ex) {
        log.warn("No data found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles validation errors for request fields.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation errors: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Errores de validación", errors,
                        java.time.Instant.now().toString()));
    }

    /**
     * Handles type mismatch errors in parameters
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parámetro inválido '%s': se esperaba %s pero se recibió '%s'",
                ex.getParameter().getParameterName(),
                ex.getParameter().getParameterType().getSimpleName(),
                ex.getValue());

        log.warn("Type mismatch: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    /**
     * Handles missing required parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = String.format("Parámetro requerido faltante: '%s' de tipo %s",
                ex.getParameterName(), ex.getParameterType());

        log.warn("Missing parameter: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    /**
     * Handles errors when the request body cannot be read
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Error al procesar el cuerpo de la petición. Verifique el formato JSON.";

        log.warn("Message not readable: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    /**
     * Handles illegal arguments
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Argumento inválido: " + ex.getMessage()));
    }

    /**
     * Handles exceptions related to data access
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccess(DataAccessException ex) {
        log.error("Database access error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error de acceso a datos. Intente nuevamente."));
    }

    /**
     * Handles SQL exceptions
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Void>> handleSQLException(SQLException ex) {
        log.error("SQL error: Code={}, State={}, Message={}",
                ex.getErrorCode(), ex.getSQLState(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error en la base de datos. Contacte al administrador."));
    }

    /**
     * Handles unsupported operations
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedOperation(UnsupportedOperationException ex) {
        log.warn("Unsupported operation: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(ApiResponse.error("Operación no soportada: " + ex.getMessage()));
    }

    /**
     * Handles unexpected exceptions that are not specifically caught
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor. Contacte al administrador."));
    }

    /**
     * Handles runtime exceptions that are not caught by more specific handlers.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
        // If it was already handled by a more specific handler, we do not process it here
        if (ex instanceof NoDataFoundException ||
                ex instanceof ReportGenerationException ||
                ex instanceof IllegalArgumentException ||
                ex instanceof UnsupportedOperationException) {
            throw ex;
        }

        log.error("Runtime error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error de ejecución: " + ex.getMessage()));
    }
}
