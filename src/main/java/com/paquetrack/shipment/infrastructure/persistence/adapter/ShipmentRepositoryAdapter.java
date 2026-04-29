package com.paquetrack.shipment.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;
import com.paquetrack.shipment.infrastructure.persistence.repository.JpaShipmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentRepositoryAdapter implements ShipmentRepositoryPort {

    private final JpaShipmentRepository jpaShipmentRepository;
    private final ShipmentMapper shipmentMapper;

    @Override
    public Shipment save(Shipment shipment) {
        log.debug("Guardando envío con trackingId: {}", shipment.getTrackingId());
        return shipmentMapper.toDomain(
                jpaShipmentRepository.save(
                        shipmentMapper.toEntity(shipment)));
    }

    @Override
    public Optional<Shipment> findById(String id) {
        log.debug("Buscando envío por id: {}", id);
        return jpaShipmentRepository.findById(id)
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