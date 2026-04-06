package com.paquetrack.shipment.domain.port.in;

import com.paquetrack.shipment.domain.model.Shipment;
import java.util.Optional;

public interface GetShipmentUseCase {
    Optional<Shipment> getShipment(String id);
}