package com.paquetrack.shipment.domain.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.paquetrack.shipment.domain.model.Shipment;

public interface ShipmentRepositoryPort {
    Shipment save(Shipment shipment);

    Optional<Shipment> findById(String id);

    Optional<Shipment> findByTrackingId(String trackingId);

    List<Shipment> findBySenderNameContaining(String senderName);
    
    List<Shipment> findByRecipientNameContaining(String recipientName);

    List<Shipment> findByStatusAndDateRange(String status, LocalDate from, LocalDate to);
    
    List<Shipment> findByDateRange(LocalDate from, LocalDate to);
}
