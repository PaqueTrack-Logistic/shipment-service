package com.paquetrack.shipment.application.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.in.CreateShipmentUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentEventPublisherPort;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateShipmentService implements CreateShipmentUseCase {

    private final ShipmentRepositoryPort repository;
    private final ShipmentEventPublisherPort publisher;  // ← nuevo

    @Override
    @Transactional
    public Shipment createShipment(Shipment shipment) {

        // 1. Construir con id y trackingId generados
        Shipment toSave = shipment.toBuilder()
                .id(UUID.randomUUID().toString())
                .trackingId(generateTrackingNumber())
                .build();

        // 2. Aplicar lógica de dominio
        Shipment ready = toSave.markAsCreated();

        // 3. Persistir
        Shipment saved = repository.save(ready);

        // 4. Publicar evento a RabbitMQ
        publisher.publishShipmentCreated(saved);

        return saved;
    }

    private String generateTrackingNumber() {
        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        return "PQ-" + date + "-" + unique;
    }
}
