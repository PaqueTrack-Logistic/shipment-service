package com.paquetrack.shipment.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import com.paquetrack.shipment.domain.exception.ShipmentNotFoundException;
import com.paquetrack.shipment.domain.port.in.UpdateShipmentStatusUseCase;
import com.paquetrack.shipment.infrastructure.config.RabbitMQConfig;
import com.paquetrack.shipment.infrastructure.dto.TrackingStatusEventDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentStatusUpdateListener {

    private final UpdateShipmentStatusUseCase updateShipmentStatusUseCase;

    @RabbitListener(queues = RabbitMQConfig.STATUS_QUEUE)
    public void onStatusUpdate(TrackingStatusEventDTO event) {

        log.info("Evento recibido — shipmentId: {} | eventType: {} | {} → {}",
                event.getShipmentId(), event.getEventType(),
                event.getPreviousStatus(), event.getNewStatus());

        validateEvent(event);

        try {
            updateShipmentStatusUseCase.updateStatus(
                    event.getShipmentId(),
                    event.getNewStatus(),
                    event.getOccurredAt());

        } catch (ShipmentNotFoundException e) {
            // Envío no existe — no tiene sentido reintentar, va directo a DLQ
            log.error("Envío no encontrado — shipmentId: {} | el mensaje irá a DLQ",
                    event.getShipmentId());
            throw new ListenerExecutionFailedException(
                    "Envío no encontrado: " + event.getShipmentId(),
                    new RuntimeException(e));

        } catch (Exception e) {
            log.error("Error procesando evento de tracking — shipmentId: {} | intento fallido |error: {}",
                    event.getShipmentId(), e.getMessage());

            throw e;
        }
    }

    private void validateEvent(TrackingStatusEventDTO event) {
        if (event.getShipmentId() == null || event.getShipmentId().isBlank()) {
            log.error("Evento inválido — shipmentId es nulo o vacío, descartando");
            throw new IllegalArgumentException("shipmentId es obligatorio");
        }
        if (event.getEventType() == null || event.getEventType().isBlank()) {
            log.error("Evento inválido — eventType es nulo o vacío | shipmentId: {}",
                    event.getShipmentId());
            throw new IllegalArgumentException("newStatus es obligatorio");
        }
    }
}