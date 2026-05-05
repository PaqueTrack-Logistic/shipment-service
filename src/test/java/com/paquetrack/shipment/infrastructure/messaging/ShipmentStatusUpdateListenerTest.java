package com.paquetrack.shipment.infrastructure.messaging;

import com.paquetrack.shipment.domain.exception.ShipmentNotFoundException;
import com.paquetrack.shipment.domain.port.in.UpdateShipmentStatusUseCase;
import com.paquetrack.shipment.infrastructure.dto.TrackingStatusEventDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentStatusUpdateListenerTest {

    @Mock
    private UpdateShipmentStatusUseCase updateShipmentStatusUseCase;

    @InjectMocks
    private ShipmentStatusUpdateListener listener;

    // ── Helpers ─────────────────────────────────────────────────────────────

    private TrackingStatusEventDTO buildValidEvent() {
        TrackingStatusEventDTO event = new TrackingStatusEventDTO();
        event.setShipmentId("shipment-abc");
        event.setTrackingId("PQ-20260407-ABC123");
        event.setEventType("DISPATCHED");
        event.setPreviousStatus("CREATED");
        event.setNewStatus("DISPATCHED");
        event.setOccurredAt("2026-04-07T10:30:00");
        return event;
    }

    // ════════════════════════════════════════════════════════════════════════
    // onStatusUpdate — camino feliz
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("onStatusUpdate calls updateStatus with correct arguments when event is valid")
    void onStatusUpdateCallsUseCaseWithValidEvent() {
        // Arrange
        TrackingStatusEventDTO event = buildValidEvent();

        // Act
        listener.onStatusUpdate(event);

        // Assert
        verify(updateShipmentStatusUseCase).updateStatus(
                event.getShipmentId(),
                event.getNewStatus(),
                event.getOccurredAt());
    }

    // ════════════════════════════════════════════════════════════════════════
    // onStatusUpdate — ShipmentNotFoundException → ListenerExecutionFailedException
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("onStatusUpdate throws ListenerExecutionFailedException when shipment not found")
    void onStatusUpdateThrowsListenerExceptionWhenShipmentNotFound() {
        // Arrange
        TrackingStatusEventDTO event = buildValidEvent();
        doThrow(new ShipmentNotFoundException("id", event.getShipmentId()))
                .when(updateShipmentStatusUseCase)
                .updateStatus(event.getShipmentId(), event.getNewStatus(), event.getOccurredAt());

        // Act
        Runnable action = () -> listener.onStatusUpdate(event);

        // Assert
        assertThatThrownBy(action::run)
                .isInstanceOf(ListenerExecutionFailedException.class)
                .hasMessageContaining(event.getShipmentId());
    }

    // ════════════════════════════════════════════════════════════════════════
    // onStatusUpdate — Exception genérica → se relanza tal cual
    // ════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("onStatusUpdate rethrows generic exception as-is")
    void onStatusUpdateRethrowsGenericException() {
        // Arrange
        TrackingStatusEventDTO event = buildValidEvent();
        RuntimeException cause = new RuntimeException("DB connection lost");
        doThrow(cause)
                .when(updateShipmentStatusUseCase)
                .updateStatus(event.getShipmentId(), event.getNewStatus(), event.getOccurredAt());

        // Act
        Runnable action = () -> listener.onStatusUpdate(event);

        // Assert
        assertThatThrownBy(action::run)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB connection lost");
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateEvent — shipmentId nulo
    // ════════════════════════════════════════════════════════════════════════
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidEventProvider")
    @DisplayName("onStatusUpdate throws IllegalArgumentException for invalid events")
    void onStatusUpdateThrowsWhenEventIsInvalid(
            String testName,
            TrackingStatusEventDTO event,
            String expectedMessage) {

        // Act
        Runnable action = () -> listener.onStatusUpdate(event);

        // Assert
        assertThatThrownBy(action::run)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(updateShipmentStatusUseCase);
    }

    private static Stream<Arguments> invalidEventProvider() {
        return Stream.of(
                Arguments.of("shipmentId is null", eventWith(null, "DISPATCHED"), "shipmentId es obligatorio"),
                Arguments.of("shipmentId is blank", eventWith("   ", "DISPATCHED"), "shipmentId es obligatorio"),
                Arguments.of("eventType is null", eventWith("shipment-abc", null), "newStatus es obligatorio"),
                Arguments.of("eventType is blank", eventWith("shipment-abc", "   "), "newStatus es obligatorio")
        );
    }

    private static TrackingStatusEventDTO eventWith(String shipmentId, String eventType) {
        TrackingStatusEventDTO event = new TrackingStatusEventDTO();
        event.setShipmentId(shipmentId);
        event.setEventType(eventType);
        event.setNewStatus("DISPATCHED");
        event.setOccurredAt("2026-04-07T10:30:00");
        return event;
    }
}
