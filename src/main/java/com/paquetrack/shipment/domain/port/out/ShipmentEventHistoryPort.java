package com.paquetrack.shipment.domain.port.out;

import java.util.List;

import com.paquetrack.shipment.infrastructure.dto.ShipmentEventHistoryResponseDTO;

public interface ShipmentEventHistoryPort {
    void save(String shipmentId, String previousStatus,
            String newStatus, String occurredAt);

    List<ShipmentEventHistoryResponseDTO> findByShipmentId(String shipmentId);
}