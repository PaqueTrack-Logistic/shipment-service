package com.paquetrack.shipment.domain.port.in;

import org.springframework.lang.NonNull;
import com.paquetrack.shipment.domain.model.Shipment;

public interface CreateShipmentUseCase {
    @NonNull
    Shipment createShipment(@NonNull Shipment shipment);
}