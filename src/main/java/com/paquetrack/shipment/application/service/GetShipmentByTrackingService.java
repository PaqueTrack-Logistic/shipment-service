package com.paquetrack.shipment.application.service;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.in.GetShipmentByTrackingUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@RequiredArgsConstructor
public class GetShipmentByTrackingService implements GetShipmentByTrackingUseCase {

    private final ShipmentRepositoryPort repository;

    @Override
    public Optional<Shipment> getShipmentByTrackingId(String trackingId) {
        return repository.findByTrackingId(trackingId);
    }
}