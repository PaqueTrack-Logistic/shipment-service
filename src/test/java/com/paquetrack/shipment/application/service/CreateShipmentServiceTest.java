package com.paquetrack.shipment.application.service;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentEventPublisherPort;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateShipmentServiceTest {

    @Mock
    private ShipmentRepositoryPort repository;

    @Mock
    private ShipmentEventPublisherPort publisher;

    @InjectMocks
    private CreateShipmentService createShipmentService;

    private Shipment buildInputShipment() {
        return Shipment.builder()
                .senderName("Juan Perez")
                .senderAddress("Calle 1")
                .senderCity("Medellin")
                .recipientName("Maria Lopez")
                .recipientAddress("Cra 2")
                .recipientCity("Bogota")
                .weightKg(new BigDecimal("2.5"))
                .build();
    }

    @Test
    @DisplayName("Successful creation sets ID, trackingId, and status CREATED")
    void createShipmentSetsIdTrackingIdAndStatus() {
        when(repository.save(any(Shipment.class))).thenAnswer(inv -> inv.getArgument(0));

        Shipment input = buildInputShipment();
        Shipment result = createShipmentService.createShipment(input);

        assertThat(result.getId()).isNotNull().isNotBlank();
        assertThat(result.getTrackingId()).isNotNull().isNotBlank();
        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getSenderName()).isEqualTo("Juan Perez");
        assertThat(result.getRecipientName()).isEqualTo("Maria Lopez");
    }

    @Test
    @DisplayName("Tracking ID follows PQ-YYYYMMDD-XXXXXX pattern")
    void trackingIdFollowsExpectedFormat() {
        when(repository.save(any(Shipment.class))).thenAnswer(inv -> inv.getArgument(0));

        Shipment result = createShipmentService.createShipment(buildInputShipment());

        String expectedDatePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        assertThat(result.getTrackingId()).matches("PQ-" + expectedDatePart + "-[A-Z0-9]{6}");
    }

    @Test
    @DisplayName("Repository save is called with the shipment")
    void repositorySaveIsCalled() {
        when(repository.save(any(Shipment.class))).thenAnswer(inv -> inv.getArgument(0));

        createShipmentService.createShipment(buildInputShipment());

        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(repository, times(1)).save(captor.capture());

        Shipment saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo("CREATED");
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTrackingId()).isNotNull();
    }

    @Test
    @DisplayName("Event publisher is called with the saved shipment")
    void eventPublisherIsCalled() {
        when(repository.save(any(Shipment.class))).thenAnswer(inv -> inv.getArgument(0));

        Shipment result = createShipmentService.createShipment(buildInputShipment());

        verify(publisher, times(1)).publishShipmentCreated(result);
    }
}
