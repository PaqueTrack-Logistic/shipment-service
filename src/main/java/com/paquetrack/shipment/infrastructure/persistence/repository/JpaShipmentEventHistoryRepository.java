package com.paquetrack.shipment.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEventHistoryEntity;

public interface JpaShipmentEventHistoryRepository
        extends JpaRepository<ShipmentEventHistoryEntity, String> {

    List<ShipmentEventHistoryEntity> findByShipmentIdOrderByRecordedAtDesc(String shipmentId);
}