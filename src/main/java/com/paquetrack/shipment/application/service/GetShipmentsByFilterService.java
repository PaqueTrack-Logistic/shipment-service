package com.paquetrack.shipment.application.service;

import java.util.List;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.in.GetShipmentsByFilterUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GetShipmentsByFilterService implements GetShipmentsByFilterUseCase {

    private final ShipmentRepositoryPort repository;

    @Override
    public List<Shipment> getBySenderName(String senderName) {
        log.debug("Buscando envíos por remitente: {}", senderName);
        return repository.findBySenderNameContaining(senderName);
    }

    @Override
    public List<Shipment> getByRecipientName(String recipientName) {
        log.debug("Buscando envíos por destinatario: {}", recipientName);
        return repository.findByRecipientNameContaining(recipientName);
    }
}