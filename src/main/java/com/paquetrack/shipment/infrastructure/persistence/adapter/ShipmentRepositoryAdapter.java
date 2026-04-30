package com.paquetrack.shipment.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;
import com.paquetrack.shipment.infrastructure.persistence.repository.JpaShipmentRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentRepositoryAdapter implements ShipmentRepositoryPort {

    private final JpaShipmentRepository jpaShipmentRepository;
    private final ShipmentMapper shipmentMapper;

    @Override
    public @NonNull Shipment save(@NonNull Shipment shipment) {
        log.debug("Guardando envío con trackingId: {}", shipment.getTrackingId());

        return Optional.of(shipment)
                .map(shipmentMapper::toEntity)
                .map(jpaShipmentRepository::save)
                .map(shipmentMapper::toDomain)
                .orElseThrow();
    }

    @Override
    public Optional<Shipment> findById(@NonNull String id) {
        log.debug("Buscando envío por id: {}", id);

        return Optional.of(id)
                .flatMap(jpaShipmentRepository::findById)
                .map(shipmentMapper::toDomain);
    }

    @Override
    public Optional<Shipment> findByTrackingId(String trackingId) {
        log.debug("Buscando envío por trackingId: {}", trackingId);
        return jpaShipmentRepository.findByTrackingId(trackingId)
                .map(shipmentMapper::toDomain);
    }

    @Override
    public List<Shipment> findBySenderNameContaining(String senderName) {
        return jpaShipmentRepository
                .findBySenderNameContainingIgnoreCase(senderName)
                .stream()
                .map(shipmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shipment> findByRecipientNameContaining(String recipientName) {
        return jpaShipmentRepository
                .findByRecipientNameContainingIgnoreCase(recipientName)
                .stream()
                .map(shipmentMapper::toDomain)
                .collect(Collectors.toList());
    }
}