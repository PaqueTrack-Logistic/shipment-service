package com.paquetrack.shipment.infrastructure.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.paquetrack.shipment.domain.exception.ShipmentNotFoundException;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── Constantes ──────────────────────────────────────────────────
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String PATH = "path";
    private static final String DETAILS = "details";
    private static final String MESSAGE = "message";

    // ─── Handlers ────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        log.warn("Error de validación en {}: {}", request.getRequestURI(), errors);

        Map<String, Object> response = buildResponse(HttpStatus.BAD_REQUEST, "Datos inválidos", request);
        response.put(DETAILS, errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ShipmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleShipmentNotFound(
            ShipmentNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Envío no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());

        Map<String, Object> response = buildResponse(HttpStatus.NOT_FOUND, "Envío no encontrado", request);
        response.put(MESSAGE, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        log.debug("Ruta no encontrada: {}", ex.getRequestURL());

        Map<String, Object> response = buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
        response.put(PATH, ex.getRequestURL());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        log.debug("Recurso estático no encontrado: {}", ex.getResourcePath());

        Map<String, Object> response = buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
        response.put(PATH, ex.getResourcePath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(
            Exception ex,
            HttpServletRequest request) {

        log.error("Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.internalServerError()
                .body(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFormat(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String message = "El cuerpo de la solicitud contiene datos inválidos";

        if (ex.getCause() instanceof InvalidFormatException ife) {
            String field = ife.getPath().isEmpty() ? "desconocido"
                    : ife.getPath().get(0).getFieldName();
            String value = String.valueOf(ife.getValue());
            String type = ife.getTargetType().getSimpleName();
            message = String.format("El campo '%s' recibió el valor '%s' pero se esperaba un %s",
                    field, value, type);
        }

        log.warn("Formato inválido en {}: {}", request.getRequestURI(), message);

        Map<String, Object> response = buildResponse(HttpStatus.BAD_REQUEST, "Formato de datos inválido", request);
        response.put(MESSAGE, message);

        return ResponseEntity.badRequest().body(response);
    }

    // ─── Método privado reutilizable ─────────────────────────────────

    private Map<String, Object> buildResponse(HttpStatus status,
            String errorMessage,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now().toString());
        response.put(STATUS, status.value());
        response.put(ERROR, errorMessage);
        response.put(PATH, request.getRequestURI());
        return response;
    }

}
