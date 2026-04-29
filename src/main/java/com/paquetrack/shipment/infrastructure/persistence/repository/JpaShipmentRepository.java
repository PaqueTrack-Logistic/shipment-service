package com.paquetrack.shipment.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEntity;

public interface JpaShipmentRepository extends JpaRepository<ShipmentEntity, String> {
    Optional<ShipmentEntity> findByTrackingId(String trackingId);

    // Case insensitive para remitente y destinatario
    List<ShipmentEntity> findBySenderNameContainingIgnoreCase(String senderName);
    List<ShipmentEntity> findByRecipientNameContainingIgnoreCase(String recipientName);

}