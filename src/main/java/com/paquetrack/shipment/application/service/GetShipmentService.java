package com.paquetrack.shipment.application.service;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.in.GetShipmentUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@RequiredArgsConstructor
public class GetShipmentService implements GetShipmentUseCase {

    private final ShipmentRepositoryPort repository;

    @Override
    public Optional<Shipment> getShipment(String id) {
        return repository.findById(id);
    }
}