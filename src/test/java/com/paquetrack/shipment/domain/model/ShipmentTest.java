package com.paquetrack.shipment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ShipmentTest {

    @Test
    @DisplayName("Builder creates Shipment with all fields correctly set")
    void builderCreatesCorrectObject() {
        LocalDateTime now = LocalDateTime.now();

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

        assertThat(shipment.getId()).isEqualTo("id-1");
        assertThat(shipment.getTrackingId()).isEqualTo("PQ-20260407-ABC123");
        assertThat(shipment.getStatus()).isEqualTo("CREATED");
        assertThat(shipment.getSenderName()).isEqualTo("Juan");
        assertThat(shipment.getSenderAddress()).isEqualTo("Calle 1");
        assertThat(shipment.getSenderCity()).isEqualTo("Medellin");
        assertThat(shipment.getRecipientName()).isEqualTo("Maria");
        assertThat(shipment.getRecipientAddress()).isEqualTo("Cra 2");
        assertThat(shipment.getRecipientCity()).isEqualTo("Bogota");
        assertThat(shipment.getWeightKg()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(shipment.getCreatedAt()).isEqualTo(now);
        assertThat(shipment.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("markAsCreated sets status to CREATED and sets timestamps")
    void markAsCreatedSetsStatusAndTimestamps() {
        Shipment shipment = Shipment.builder()
                .senderName("Juan")
                .recipientName("Maria")
                .build();

        LocalDateTime before = LocalDateTime.now();
        Shipment created = shipment.markAsCreated();
        LocalDateTime after = LocalDateTime.now();

        assertThat(created.getStatus()).isEqualTo("CREATED");
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();
        assertThat(created.getCreatedAt()).isEqualTo(created.getUpdatedAt());
        assertThat(created.getCreatedAt()).isAfterOrEqualTo(before);
        assertThat(created.getCreatedAt()).isBeforeOrEqualTo(after);
        // Original should be unmodified (immutable pattern)
        assertThat(shipment.getStatus()).isNull();
        assertThat(shipment.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("updateStatus changes status and updates timestamp")
    void updateStatusChangesStatusAndUpdatesTimestamp() {
        LocalDateTime originalTime = LocalDateTime.of(2026, 4, 1, 10, 0);
        Shipment shipment = Shipment.builder()
                .status("CREATED")
                .createdAt(originalTime)
                .updatedAt(originalTime)
                .build();

        LocalDateTime before = LocalDateTime.now();
        Shipment updated = shipment.updateStatus("IN_TRANSIT");
        LocalDateTime afterUpdate = LocalDateTime.now();

        assertThat(updated.getStatus()).isEqualTo("IN_TRANSIT");
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(before);
        assertThat(updated.getUpdatedAt()).isBeforeOrEqualTo(afterUpdate);
        assertThat(updated.getCreatedAt()).isEqualTo(originalTime);
        // Original unchanged
        assertThat(shipment.getStatus()).isEqualTo("CREATED");
    }
}
