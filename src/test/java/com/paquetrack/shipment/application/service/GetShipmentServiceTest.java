package com.paquetrack.shipment.application.service;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetShipmentServiceTest {

    @Mock
    private ShipmentRepositoryPort repository;

    @InjectMocks
    private GetShipmentService getShipmentService;

    @Test
    @DisplayName("Returns shipment when found by ID")
    void returnsShipmentWhenFound() {
        Shipment shipment = Shipment.builder()
                .id("abc-123")
                .trackingId("PQ-20260407-XYZ789")
                .status("CREATED")
                .build();

        when(repository.findById("abc-123")).thenReturn(Optional.of(shipment));

        Optional<Shipment> result = getShipmentService.getShipment("abc-123");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("abc-123");
        assertThat(result.get().getTrackingId()).isEqualTo("PQ-20260407-XYZ789");
    }

    @Test
    @DisplayName("Returns empty Optional when not found by ID")
    void returnsEmptyWhenNotFound() {
        when(repository.findById("non-existent")).thenReturn(Optional.empty());

        Optional<Shipment> result = getShipmentService.getShipment("non-existent");

        assertThat(result).isEmpty();
    }
}
