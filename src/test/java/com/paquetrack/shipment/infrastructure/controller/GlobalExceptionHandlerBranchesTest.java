package com.paquetrack.shipment.infrastructure.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.paquetrack.shipment.domain.exception.InvalidSearchParameterException;
import com.paquetrack.shipment.domain.exception.ShipmentNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Cubre los handlers no ejercitados por el test base (que usa MockMvc):
 * shipmentNotFound, noResourceFound, invalidSearchParameter, typeMismatch e
 * invalidFormat (con y sin InvalidFormatException como causa).
 */
class GlobalExceptionHandlerBranchesTest {

	private GlobalExceptionHandler handler;
	private HttpServletRequest request;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
		request = mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("/api/shipments/test");
	}

	@Test
	void handleShipmentNotFound_returns404() {
		ResponseEntity<Map<String, Object>> response =
				handler.handleShipmentNotFound(new ShipmentNotFoundException("id", "abc"), request);

		assertThat(response.getStatusCode().value()).isEqualTo(404);
		assertThat(response.getBody()).containsKey("message");
		assertThat(response.getBody().get("error")).isEqualTo("Envío no encontrado");
	}

	@Test
	void handleNoResourceFound_returns404WithPath() {
		NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/static/app.js");

		ResponseEntity<Map<String, Object>> response = handler.handleNoResourceFound(ex, request);

		assertThat(response.getStatusCode().value()).isEqualTo(404);
		assertThat(response.getBody().get("path")).isEqualTo("/static/app.js");
	}

	@Test
	void handleInvalidSearchParameter_returns400() {
		ResponseEntity<Map<String, Object>> response = handler.handleInvalidSearchParameter(
				new InvalidSearchParameterException("Solo un parámetro a la vez"), request);

		assertThat(response.getStatusCode().value()).isEqualTo(400);
		assertThat(response.getBody().get("message")).isEqualTo("Solo un parámetro a la vez");
	}

	@Test
	void handleTypeMismatch_returns400WithFieldName() {
		MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
				"no-fecha", java.time.LocalDate.class, "from", null, new IllegalArgumentException("x"));

		ResponseEntity<Map<String, Object>> response = handler.handleTypeMismatch(ex, request);

		assertThat(response.getStatusCode().value()).isEqualTo(400);
		assertThat(String.valueOf(response.getBody().get("message"))).contains("from");
	}

	@Test
	void handleInvalidFormat_genericWhenCauseIsNotInvalidFormat() {
		HttpMessageNotReadableException ex =
				new HttpMessageNotReadableException("cuerpo ilegible", mock(HttpInputMessage.class));

		ResponseEntity<Map<String, Object>> response = handler.handleInvalidFormat(ex, request);

		assertThat(response.getStatusCode().value()).isEqualTo(400);
		assertThat(response.getBody().get("error")).isEqualTo("Formato de datos inválido");
	}

	@Test
	void handleInvalidFormat_detailedWhenCauseIsInvalidFormat() {
		InvalidFormatException ife = InvalidFormatException.from(null, "valor inválido", "abc", BigDecimal.class);
		HttpMessageNotReadableException ex =
				new HttpMessageNotReadableException("msg", ife, mock(HttpInputMessage.class));

		ResponseEntity<Map<String, Object>> response = handler.handleInvalidFormat(ex, request);

		assertThat(response.getStatusCode().value()).isEqualTo(400);
		assertThat(String.valueOf(response.getBody().get("message"))).contains("BigDecimal");
	}
}
