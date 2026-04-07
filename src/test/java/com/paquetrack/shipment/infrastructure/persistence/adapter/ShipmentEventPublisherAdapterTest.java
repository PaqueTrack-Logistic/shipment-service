package com.paquetrack.shipment.infrastructure.persistence.adapter;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentEventPublisherAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ShipmentEventPublisherAdapter adapter;

    private Shipment buildShipment() {
        return Shipment.builder()
                .id("id-1")
                .trackingId("PQ-20260407-ABC123")
                .status("CREATED")
                .senderName("Juan")
                .senderAddress("Calle 1")
                .senderCity("Medellin")
                .recipientName("Maria")
                .recipientAddress("Cra 2")
                .recipientCity("Bogota")
                .weightKg(new BigDecimal("2.5"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("publishShipmentCreated sends message to correct exchange and routing key")
    void publishShipmentCreatedSendsToCorrectExchangeAndRoutingKey() {
        Shipment shipment = buildShipment();

        adapter.publishShipmentCreated(shipment);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq(RabbitMQConfig.ROUTING_KEY),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("publishShipmentCreated does not throw when RabbitMQ is down")
    void publishShipmentCreatedDoesNotThrowWhenRabbitMQIsDown() {
        Shipment shipment = buildShipment();

        doThrow(new AmqpException("Connection refused"))
                .when(rabbitTemplate).convertAndSend(
                        eq(RabbitMQConfig.EXCHANGE),
                        eq(RabbitMQConfig.ROUTING_KEY),
                        any(Map.class)
                );

        assertThatNoException().isThrownBy(() -> adapter.publishShipmentCreated(shipment));
    }
}
