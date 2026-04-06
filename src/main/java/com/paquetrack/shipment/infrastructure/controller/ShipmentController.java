package com.paquetrack.shipment.infrastructure.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.in.CreateShipmentUseCase;
import com.paquetrack.shipment.domain.port.in.GetShipmentByTrackingUseCase;
import com.paquetrack.shipment.domain.port.in.GetShipmentUseCase;
import com.paquetrack.shipment.infrastructure.dto.ShipmentRequestDTO;
import com.paquetrack.shipment.infrastructure.dto.ShipmentResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final CreateShipmentUseCase createShipmentUseCase;
    private final GetShipmentUseCase getShipmentUseCase;
    private final GetShipmentByTrackingUseCase getShipmentByTrackingUseCase;
    private final ShipmentMapper shipmentMapper;

    @PostMapping
    public ResponseEntity<ShipmentResponseDTO> createShipment(
            @Valid @RequestBody ShipmentRequestDTO requestDTO) {

        log.info("Creando envío para remitente: {}", requestDTO.getSenderName());

        Shipment shipment = shipmentMapper.toDomain(requestDTO);
        Shipment created = createShipmentUseCase.createShipment(shipment);

        log.info("Envío creado con trackingId: {}", created.getTrackingId());

        return ResponseEntity
                .created(URI.create("/api/shipments/" + created.getId()))
                .body(shipmentMapper.toResponseDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDTO> getShipment(
            @PathVariable String id) {

        log.info("Consultando envío por id: {}", id);

        return getShipmentUseCase.getShipment(id)
                .map(shipmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingId}")
    public ResponseEntity<ShipmentResponseDTO> getShipmentByTrackingId(
            @PathVariable String trackingId) {

        log.info("Consultando envío por trackingId: {}", trackingId);

        return getShipmentByTrackingUseCase.getShipmentByTrackingId(trackingId)
                .map(shipmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}