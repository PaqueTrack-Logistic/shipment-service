package com.paquetrack.shipment.infrastructure.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── Constantes ──────────────────────────────────────────────────
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS    = "status";
    private static final String ERROR     = "error";
    private static final String PATH      = "path";
    private static final String DETAILS   = "details";

    // ─── Handlers ────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        log.warn("Error de validación: {}", errors);

        Map<String, Object> response = buildResponse(HttpStatus.BAD_REQUEST, "Validation failed");
        response.put(DETAILS, errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(
            NoHandlerFoundException ex) {

        log.debug("Recurso no encontrado: {}", ex.getRequestURL());

        Map<String, Object> response = buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado");
        response.put(PATH, ex.getRequestURL());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            NoResourceFoundException ex) {

        log.debug("Recurso estático no encontrado: {}", ex.getResourcePath());

        Map<String, Object> response = buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado");
        response.put(PATH, ex.getResourcePath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(
            Exception ex,
            HttpServletRequest request) {

        log.error("Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.internalServerError()
                .body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor"));
    }

    // ─── Método privado reutilizable ─────────────────────────────────

    private Map<String, Object> buildResponse(HttpStatus status, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now().toString());
        response.put(STATUS, status.value());
        response.put(ERROR, errorMessage);
        return response;
    }
}
