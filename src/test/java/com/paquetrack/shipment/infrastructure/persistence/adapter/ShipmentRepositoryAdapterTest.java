package com.paquetrack.shipment.infrastructure.persistence.adapter;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEntity;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;
import com.paquetrack.shipment.infrastructure.persistence.repository.JpaShipmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentRepositoryAdapterTest {

    @Mock
    private JpaShipmentRepository jpaShipmentRepository;

    @Mock
    private ShipmentMapper shipmentMapper;

    @InjectMocks
    private ShipmentRepositoryAdapter adapter;

    private Shipment buildDomainShipment() {
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

    private ShipmentEntity buildEntity() {
        return ShipmentEntity.builder()
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
    @DisplayName("save maps domain to entity, persists, and returns mapped domain")
    void saveMapsAndPersists() {
        Shipment domain = buildDomainShipment();
        ShipmentEntity entity = buildEntity();

        when(shipmentMapper.toEntity(domain)).thenReturn(entity);
        when(jpaShipmentRepository.save(entity)).thenReturn(entity);
        when(shipmentMapper.toDomain(entity)).thenReturn(domain);

        Shipment result = adapter.save(domain);

        assertThat(result).isEqualTo(domain);
        verify(shipmentMapper).toEntity(domain);
        verify(jpaShipmentRepository).save(entity);
        verify(shipmentMapper).toDomain(entity);
    }

    @Test
    @DisplayName("findById returns mapped domain when entity exists")
    void findByIdReturnsMappedDomain() {
        ShipmentEntity entity = buildEntity();
        Shipment domain = buildDomainShipment();

        when(jpaShipmentRepository.findById("id-1")).thenReturn(Optional.of(entity));
        when(shipmentMapper.toDomain(entity)).thenReturn(domain);

        Optional<Shipment> result = adapter.findById("id-1");

        assertThat(result)
                .isPresent()
                .contains(domain);
        verify(jpaShipmentRepository).findById("id-1");
    }

    @Test
    @DisplayName("findById returns empty when entity not found")
    void findByIdReturnsEmptyWhenNotFound() {
        when(jpaShipmentRepository.findById("non-existent")).thenReturn(Optional.empty());

        Optional<Shipment> result = adapter.findById("non-existent");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByTrackingId returns mapped domain when entity exists")
    void findByTrackingIdReturnsMappedDomain() {
        ShipmentEntity entity = buildEntity();
        Shipment domain = buildDomainShipment();

        when(jpaShipmentRepository.findByTrackingId("PQ-20260407-ABC123")).thenReturn(Optional.of(entity));
        when(shipmentMapper.toDomain(entity)).thenReturn(domain);

        Optional<Shipment> result = adapter.findByTrackingId("PQ-20260407-ABC123");

        assertThat(result)
                .isPresent()
                .contains(domain);
        verify(jpaShipmentRepository).findByTrackingId("PQ-20260407-ABC123");
    }

    @Test
    @DisplayName("findByTrackingId returns empty when entity not found")
    void findByTrackingIdReturnsEmptyWhenNotFound() {
        when(jpaShipmentRepository.findByTrackingId("PQ-00000000-NOEXIST")).thenReturn(Optional.empty());

        Optional<Shipment> result = adapter.findByTrackingId("PQ-00000000-NOEXIST");

        assertThat(result).isEmpty();
    }
}
