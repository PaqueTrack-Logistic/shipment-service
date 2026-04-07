package com.paquetrack.shipment.infrastructure.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de validación (@NotBlank, @Positive, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("details", errors);

        log.warn("Error de validación: {}", errors);

        return ResponseEntity.badRequest().body(response);
    }

    // Manejo de recursos no encontrados
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFound(NoHandlerFoundException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Recurso no encontrado");
        response.put("path", ex.getRequestURL());

        log.debug("Recurso no encontrado: {}", ex.getRequestURL());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Manejo de recursos estaticos no encontrados (ej. GET / o /favicon.ico)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Recurso no encontrado");
        response.put("path", ex.getResourcePath());

        log.debug("Recurso estatico no encontrado: {}", ex.getResourcePath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Error genérico — cualquier excepción no manejada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {

        log.error("Error inesperado: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error interno del servidor");

        return ResponseEntity.internalServerError().body(response);
    }
}