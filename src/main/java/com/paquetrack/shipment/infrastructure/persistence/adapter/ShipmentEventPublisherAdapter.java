package com.paquetrack.shipment.infrastructure.persistence.adapter;

import java.util.Map;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentEventPublisherPort;
import com.paquetrack.shipment.infrastructure.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventPublisherAdapter implements ShipmentEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishShipmentCreated(Shipment shipment) {

        // Estructura exacta que espera el microservicio de tracking
        Map<String, Object> event = Map.of(
                "shipmentId",    shipment.getId(),
                "trackingId",    shipment.getTrackingId(),
                "senderName",    shipment.getSenderName(),
                "recipientName", shipment.getRecipientName()
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    event
            );
            log.info("Evento publicado a RabbitMQ — trackingId: {} | shipmentId: {}",
                    shipment.getTrackingId(), shipment.getId());

        } catch (AmqpException e) {
            // Log del error pero no rompe el flujo — el envío ya fue guardado en BD
            log.error("Error al publicar evento a RabbitMQ — trackingId: {} | error: {}",
                    shipment.getTrackingId(), e.getMessage());
        }
    }
}