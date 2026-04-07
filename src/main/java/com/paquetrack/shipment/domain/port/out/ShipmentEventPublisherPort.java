package com.paquetrack.shipment.domain.port.out;

import com.paquetrack.shipment.domain.model.Shipment;

public interface ShipmentEventPublisherPort {
    void publishShipmentCreated(Shipment shipment);
}
