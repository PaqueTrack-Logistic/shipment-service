package com.paquetrack.shipment.domain.port.out;

import java.util.Optional;

import com.paquetrack.shipment.domain.model.Shipment;

public interface ShipmentRepositoryPort {
    Shipment save(Shipment shipment);

    Optional<Shipment> findById(String id);

    Optional<Shipment> findByTrackingId(String trackingId);
}
