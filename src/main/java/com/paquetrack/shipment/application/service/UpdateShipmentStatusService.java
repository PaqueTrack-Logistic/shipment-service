package com.paquetrack.shipment.application.service;

import com.paquetrack.shipment.domain.exception.ShipmentNotFoundException;
import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.in.UpdateShipmentStatusUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentEventHistoryPort;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UpdateShipmentStatusService implements UpdateShipmentStatusUseCase {

    private final ShipmentRepositoryPort repository;
    private final ShipmentEventHistoryPort historyPort;

    @Override
    public void updateStatus(String shipmentId, String newStatus, String occurredAt) {

        // 1. Buscar el envío
        Shipment shipment = repository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException("id", shipmentId));

        String previousStatus = shipment.getStatus();

        // 2. Actualizar status con el valor que manda tracking directamente
        Shipment updated = shipment.toBuilder()
                .status(newStatus)
                .build();

        repository.save(updated);

        // 3. Guardar en historial
        historyPort.save(shipmentId, previousStatus, newStatus, occurredAt);

        // 4. Confirmar por log
        log.info("Estado actualizado — shipmentId: {} | {} → {} | occurredAt: {}",
                shipmentId, previousStatus, newStatus, occurredAt);
    }
}