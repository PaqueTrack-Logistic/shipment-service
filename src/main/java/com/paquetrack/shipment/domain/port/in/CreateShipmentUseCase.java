package com.paquetrack.shipment.domain.port.in;

import com.paquetrack.shipment.domain.model.Shipment;

public interface CreateShipmentUseCase {
    Shipment createShipment(Shipment shipment);
}