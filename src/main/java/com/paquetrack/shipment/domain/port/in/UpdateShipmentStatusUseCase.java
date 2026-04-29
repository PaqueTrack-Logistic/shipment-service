package com.paquetrack.shipment.domain.port.in;

public interface UpdateShipmentStatusUseCase {
    void updateStatus(String shipmentId, String newStatus, String occurredAt);
}