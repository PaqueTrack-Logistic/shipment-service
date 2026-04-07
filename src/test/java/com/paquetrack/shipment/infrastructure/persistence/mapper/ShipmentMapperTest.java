package com.paquetrack.shipment.infrastructure.persistence.mapper;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.infrastructure.dto.ShipmentRequestDTO;
import com.paquetrack.shipment.infrastructure.dto.ShipmentResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ShipmentMapperTest {

    private final ShipmentMapper mapper = new ShipmentMapper();

    @Test
    @DisplayName("toDomain from ShipmentRequestDTO maps all fields")
    void toDomainFromRequestDTOMapsAllFields() {
        ShipmentRequestDTO dto = ShipmentRequestDTO.builder()
                .senderName("Juan")
                .senderAddress("Calle 1")
                .senderCity("Medellin")
                .recipientName("Maria")
                .recipientAddress("Cra 2")
                .recipientCity("Bogota")
                .weightKg(new BigDecimal("3.0"))
                .build();

        Shipment result = mapper.toDomain(dto);

        assertThat(result.getSenderName()).isEqualTo("Juan");
        assertThat(result.getSenderAddress()).isEqualTo("Calle 1");
        assertThat(result.getSenderCity()).isEqualTo("Medellin");
        assertThat(result.getRecipientName()).isEqualTo("Maria");
        assertThat(result.getRecipientAddress()).isEqualTo("Cra 2");
        assertThat(result.getRecipientCity()).isEqualTo("Bogota");
        assertThat(result.getWeightKg()).isEqualByComparingTo(new BigDecimal("3.0"));
        // These should not be set from request DTO
        assertThat(result.getId()).isNull();
        assertThat(result.getTrackingId()).isNull();
        assertThat(result.getStatus()).isNull();
    }

    @Test
    @DisplayName("toResponseDTO maps all fields including dates")
    void toResponseDTOMapsAllFields() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 7, 10, 30, 0);
        Shipment shipment = Shipment.builder()
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
                .createdAt(now)
                .updatedAt(now)
                .build();

        ShipmentResponseDTO result = mapper.toResponseDTO(shipment);

        assertThat(result.getId()).isEqualTo("id-1");
        assertThat(result.getTrackingId()).isEqualTo("PQ-20260407-ABC123");
        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getSenderName()).isEqualTo("Juan");
        assertThat(result.getSenderAddress()).isEqualTo("Calle 1");
        assertThat(result.getSenderCity()).isEqualTo("Medellin");
        assertThat(result.getRecipientName()).isEqualTo("Maria");
        assertThat(result.getRecipientAddress()).isEqualTo("Cra 2");
        assertThat(result.getRecipientCity()).isEqualTo("Bogota");
        assertThat(result.getWeightKg()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toEntity maps all fields from domain model")
    void toEntityMapsAllFields() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 7, 10, 30, 0);
        Shipment shipment = Shipment.builder()
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
                .createdAt(now)
                .updatedAt(now)
                .build();

        ShipmentEntity result = mapper.toEntity(shipment);

        assertThat(result.getId()).isEqualTo("id-1");
        assertThat(result.getTrackingId()).isEqualTo("PQ-20260407-ABC123");
        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getSenderName()).isEqualTo("Juan");
        assertThat(result.getSenderAddress()).isEqualTo("Calle 1");
        assertThat(result.getSenderCity()).isEqualTo("Medellin");
        assertThat(result.getRecipientName()).isEqualTo("Maria");
        assertThat(result.getRecipientAddress()).isEqualTo("Cra 2");
        assertThat(result.getRecipientCity()).isEqualTo("Bogota");
        assertThat(result.getWeightKg()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toDomain from ShipmentEntity maps all fields")
    void toDomainFromEntityMapsAllFields() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 7, 10, 30, 0);
        ShipmentEntity entity = ShipmentEntity.builder()
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
                .createdAt(now)
                .updatedAt(now)
                .build();

        Shipment result = mapper.toDomain(entity);

        assertThat(result.getId()).isEqualTo("id-1");
        assertThat(result.getTrackingId()).isEqualTo("PQ-20260407-ABC123");
        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getSenderName()).isEqualTo("Juan");
        assertThat(result.getSenderAddress()).isEqualTo("Calle 1");
        assertThat(result.getSenderCity()).isEqualTo("Medellin");
        assertThat(result.getRecipientName()).isEqualTo("Maria");
        assertThat(result.getRecipientAddress()).isEqualTo("Cra 2");
        assertThat(result.getRecipientCity()).isEqualTo("Bogota");
        assertThat(result.getWeightKg()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.getUpdatedAt()).isEqualTo(now);
    }
}
