package com.paquetrack.shipment.infrastructure.persistence.adapter;

import com.paquetrack.shipment.infrastructure.dto.ShipmentEventHistoryResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEventHistoryEntity;
import com.paquetrack.shipment.infrastructure.persistence.repository.JpaShipmentEventHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentEventHistoryAdapterTest {


    @Mock
    private JpaShipmentEventHistoryRepository repository;

    @InjectMocks
    private ShipmentEventHistoryAdapter adapter;

    private ShipmentEventHistoryEntity buildEntity() {
        return new ShipmentEventHistoryEntity(
                "evt-001",
                "shipment-abc",
                "CREATED",
                "DISPATCHED",
                "Medellin HUB",
                LocalDateTime.of(2026, 4, 7, 10, 30, 0));
    }

    @Test
    @DisplayName("save persists entity with correct field values")
    void savePersistsEntityWithCorrectFields() {
        // Arrange
        ArgumentCaptor<ShipmentEventHistoryEntity> captor =
                ArgumentCaptor.forClass(ShipmentEventHistoryEntity.class);

        // Act
        adapter.save("shipment-abc", "CREATED", "DISPATCHED", "Medellin HUB");

        // Assert
        verify(repository).save(captor.capture());
        ShipmentEventHistoryEntity saved = captor.getValue();

        assertThat(saved.getShipmentId()).isEqualTo("shipment-abc");
        assertThat(saved.getPreviousStatus()).isEqualTo("CREATED");
        assertThat(saved.getNewStatus()).isEqualTo("DISPATCHED");
        assertThat(saved.getOccurredAt()).isEqualTo("Medellin HUB");
        assertThat(saved.getId()).isNotBlank();           // UUID generado
        assertThat(saved.getRecordedAt()).isNotNull();    // LocalDateTime.now()
    }

    @Test
    @DisplayName("findByShipmentId returns mapped DTOs ordered by recordedAt desc")
    void findByShipmentIdReturnsMappedDTOs() {
        // Arrange
        ShipmentEventHistoryEntity entity = buildEntity();
        when(repository.findByShipmentIdOrderByRecordedAtDesc("shipment-abc"))
                .thenReturn(List.of(entity));

        // Act
        List<ShipmentEventHistoryResponseDTO> result = adapter.findByShipmentId("shipment-abc");

        // Assert
        assertThat(result).hasSize(1);
        ShipmentEventHistoryResponseDTO dto = result.getFirst();
        assertThat(dto.getId()).isEqualTo("evt-001");
        assertThat(dto.getShipmentId()).isEqualTo("shipment-abc");
        assertThat(dto.getPreviousStatus()).isEqualTo("CREATED");
        assertThat(dto.getNewStatus()).isEqualTo("DISPATCHED");
        assertThat(dto.getOccurredAt()).isEqualTo("Medellin HUB");
        assertThat(dto.getRecordedAt()).isEqualTo(LocalDateTime.of(2026, 4, 7, 10, 30, 0));

        verify(repository).findByShipmentIdOrderByRecordedAtDesc("shipment-abc");
    }

    @Test
    @DisplayName("findByShipmentId returns empty list when no events exist")
    void findByShipmentIdReturnsEmptyList() {
        // Arrange
        when(repository.findByShipmentIdOrderByRecordedAtDesc("shipment-abc"))
                .thenReturn(Collections.emptyList());

        // Act
        List<ShipmentEventHistoryResponseDTO> result = adapter.findByShipmentId("shipment-abc");

        // Assert
        assertThat(result).isEmpty();
    }
}
