package com.backend.orchestrator.orchestrator_service.exceptions;

import com.backend.orchestrator.orchestrator_service.dto.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: code={} message={} status={}",
                ex.getErrorCode(), ex.getMessage(), ex.getHttpStatus());
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ErrorResponseDTO(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Datos inválidos");

        log.warn("Validación fallida: {}", message);
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        log.error("Error inesperado", ex);
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponseDTO("INTERNAL_ERROR", "Error interno del servidor"));
    }
}
