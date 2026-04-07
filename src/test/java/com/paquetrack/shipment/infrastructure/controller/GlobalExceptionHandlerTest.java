package com.paquetrack.shipment.infrastructure.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class FakeController {

        @PostMapping("/fake/validation")
        public void throwValidation(@Valid @RequestBody ValidationDto dto) {
            // validation triggers MethodArgumentNotValidException
        }

        @GetMapping("/fake/no-handler")
        public void throwNoHandler() throws NoHandlerFoundException {
            throw new NoHandlerFoundException("GET", "/unknown/path", null);
        }

        @GetMapping("/fake/generic")
        public void throwGeneric() {
            throw new RuntimeException("Unexpected error");
        }
    }

    record ValidationDto(@NotBlank String name) {}

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FakeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleValidationErrors_returns400WithDetails() throws Exception {
        mockMvc.perform(post("/fake/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details").isMap())
                .andExpect(jsonPath("$.details.name").exists());
    }

    @Test
    void handleNoHandlerFound_returns404() throws Exception {
        mockMvc.perform(get("/fake/no-handler"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.path").value("/unknown/path"));
    }

    @Test
    void handleGenericError_returns500() throws Exception {
        mockMvc.perform(get("/fake/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Error interno del servidor"));
    }
}
