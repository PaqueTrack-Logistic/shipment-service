package com.paquetrack.shipment.domain.port.in;

import java.util.List;

import com.paquetrack.shipment.domain.model.Shipment;

public interface GetShipmentsByFilterUseCase {
    List<Shipment> getBySenderName(String senderName);
    List<Shipment> getByRecipientName(String recipientName);
}