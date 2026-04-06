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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Operaciones para crear y consultar envios")
public class ShipmentController {

    private final CreateShipmentUseCase createShipmentUseCase;
    private final GetShipmentUseCase getShipmentUseCase;
    private final GetShipmentByTrackingUseCase getShipmentByTrackingUseCase;
    private final ShipmentMapper shipmentMapper;

    @PostMapping
    @Operation(summary = "Crear envio", description = "Registra un nuevo envio y genera trackingId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envio creado", content = @Content(schema = @Schema(implementation = ShipmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
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
    @Operation(summary = "Consultar envio por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envio encontrado", content = @Content(schema = @Schema(implementation = ShipmentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envio no encontrado")
    })
    public ResponseEntity<ShipmentResponseDTO> getShipment(
            @Parameter(description = "ID unico del envio", example = "4ff2a911-5a9e-4fd9-8f84-d9f6f020f66f")
            @PathVariable String id) {

        log.info("Consultando envío por id: {}", id);

        return getShipmentUseCase.getShipment(id)
                .map(shipmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingId}")
    @Operation(summary = "Consultar envio por trackingId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envio encontrado", content = @Content(schema = @Schema(implementation = ShipmentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envio no encontrado")
    })
    public ResponseEntity<ShipmentResponseDTO> getShipmentByTrackingId(
            @Parameter(description = "TrackingId del envio", example = "PQ-20260406-ABC123")
            @PathVariable String trackingId) {

        log.info("Consultando envío por trackingId: {}", trackingId);

        return getShipmentByTrackingUseCase.getShipmentByTrackingId(trackingId)
                .map(shipmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}