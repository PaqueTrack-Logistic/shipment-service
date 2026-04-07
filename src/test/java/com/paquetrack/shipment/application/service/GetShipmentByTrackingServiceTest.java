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
class GetShipmentByTrackingServiceTest {

    @Mock
    private ShipmentRepositoryPort repository;

    @InjectMocks
    private GetShipmentByTrackingService getShipmentByTrackingService;

    @Test
    @DisplayName("Returns shipment when found by tracking ID")
    void returnsShipmentWhenFoundByTrackingId() {
        Shipment shipment = Shipment.builder()
                .id("abc-123")
                .trackingId("PQ-20260407-XYZ789")
                .status("CREATED")
                .build();

        when(repository.findByTrackingId("PQ-20260407-XYZ789")).thenReturn(Optional.of(shipment));

        Optional<Shipment> result = getShipmentByTrackingService.getShipmentByTrackingId("PQ-20260407-XYZ789");

        assertThat(result).isPresent();
        assertThat(result.get().getTrackingId()).isEqualTo("PQ-20260407-XYZ789");
        assertThat(result.get().getId()).isEqualTo("abc-123");
    }

    @Test
    @DisplayName("Returns empty Optional when not found by tracking ID")
    void returnsEmptyWhenNotFoundByTrackingId() {
        when(repository.findByTrackingId("PQ-00000000-NOEXIST")).thenReturn(Optional.empty());

        Optional<Shipment> result = getShipmentByTrackingService.getShipmentByTrackingId("PQ-00000000-NOEXIST");

        assertThat(result).isEmpty();
    }
}
