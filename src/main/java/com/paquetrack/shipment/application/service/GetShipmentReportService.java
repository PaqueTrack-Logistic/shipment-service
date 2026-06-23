package com.paquetrack.shipment.application.service;

import java.time.LocalDate;
import java.util.List;

import com.paquetrack.shipment.domain.port.in.GetShipmentReportUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import com.paquetrack.shipment.infrastructure.dto.ShipmentReportResponseDTO;
import com.paquetrack.shipment.infrastructure.dto.ShipmentResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GetShipmentReportService implements GetShipmentReportUseCase {

    private final ShipmentRepositoryPort repository;
    private final ShipmentMapper shipmentMapper;

    @Override
    public ShipmentReportResponseDTO getReport(LocalDate from, LocalDate to) {

        log.debug("Generando reporte de envíos del {} al {}", from, to);

        List<ShipmentResponseDTO> created = toDTO(
                repository.findByStatusAndDateRange("CREATED", from, to));

        List<ShipmentResponseDTO> inTransit = toDTO(
                repository.findByStatusAndDateRange("IN_TRANSIT", from, to));

        List<ShipmentResponseDTO> outForDelivery = toDTO(
                repository.findByStatusAndDateRange("OUT_FOR_DELIVERY", from, to));

        List<ShipmentResponseDTO> delivered = toDTO(
                repository.findByStatusAndDateRange("DELIVERED", from, to));

        List<ShipmentResponseDTO> exception = toDTO(
                repository.findByStatusAndDateRange("EXCEPTION", from, to));

        long total = (long) created.size() + inTransit.size() +
                     outForDelivery.size() + delivered.size() + exception.size();

        log.info("Reporte generado del {} al {} — total: {}", from, to, total);

        return ShipmentReportResponseDTO.builder()
                .from(from)
                .to(to)
                .totalCreated(created.size())
                .totalInTransit(inTransit.size())
                .totalOutForDelivery(outForDelivery.size())
                .totalDelivered(delivered.size())
                .totalException(exception.size())
                .totalGeneral(total)
                .created(created)
                .inTransit(inTransit)
                .outForDelivery(outForDelivery)
                .delivered(delivered)
                .exception(exception)
                .build();
    }

    private List<ShipmentResponseDTO> toDTO(
            java.util.List<com.paquetrack.shipment.domain.model.Shipment> shipments) {
        return shipments.stream()
                .map(shipmentMapper::toResponseDTO)
                .toList();
    }
}