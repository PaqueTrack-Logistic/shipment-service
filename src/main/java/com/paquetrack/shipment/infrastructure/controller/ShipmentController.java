package com.paquetrack.shipment.infrastructure.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Gestión de envíos y números de guía")
public class ShipmentController {

    private final CreateShipmentUseCase createShipmentUseCase;
    private final GetShipmentUseCase getShipmentUseCase;
    private final GetShipmentByTrackingUseCase getShipmentByTrackingUseCase;
    private final ShipmentMapper shipmentMapper;

    @Operation(
            summary = "Crear un nuevo envío",
            description = "Crea un envío y genera automáticamente un número de guía único. " +
                    "También publica un evento al servicio de tracking."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Envío creado exitosamente",
                    content = @Content(schema = @Schema(implementation = ShipmentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
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

    @Operation(
            summary = "Consultar envío por ID",
            description = "Retorna los detalles de un envío dado su ID interno."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío encontrado",
                    content = @Content(schema = @Schema(implementation = ShipmentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDTO> getShipment(
            @Parameter(description = "ID interno del envío", required = true)
            @PathVariable String id) {

        log.info("Consultando envío por id: {}", id);
        return getShipmentUseCase.getShipment(id)
                .map(shipmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Consultar envío por número de guía",
            description = "Retorna los detalles de un envío dado su número de guía. " +
                    "Formato esperado: PQ-YYYYMMDD-XXXXXX"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío encontrado",
                    content = @Content(schema = @Schema(implementation = ShipmentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @GetMapping("/tracking/{trackingId}")
    public ResponseEntity<ShipmentResponseDTO> getShipmentByTrackingId(
            @Parameter(description = "Número de guía del envío. Ejemplo: PQ-20240401-A3F9C2",
                    required = true)
            @PathVariable String trackingId) {

        log.info("Consultando envío por trackingId: {}", trackingId);
        return getShipmentByTrackingUseCase.getShipmentByTrackingId(trackingId)
                .map(shipmentMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}