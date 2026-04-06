package com.paquetrack.shipment.infrastructure.persistence.repository;

import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaShipmentRepository extends JpaRepository<ShipmentEntity, String> {
    Optional<ShipmentEntity> findByTrackingId(String trackingId);
}