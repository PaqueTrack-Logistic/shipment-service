package com.paquetrack.shipment.domain.port.in;

import com.paquetrack.shipment.domain.model.Shipment;
import java.util.Optional;

public interface GetShipmentByTrackingUseCase {
    Optional<Shipment> getShipmentByTrackingId(String trackingId);
}