package com.paquetrack.shipment.domain.port.in;

import com.paquetrack.shipment.infrastructure.dto.ShipmentReportResponseDTO;
import java.time.LocalDate;

public interface GetShipmentReportUseCase {
    ShipmentReportResponseDTO getReport(LocalDate from, LocalDate to);
}