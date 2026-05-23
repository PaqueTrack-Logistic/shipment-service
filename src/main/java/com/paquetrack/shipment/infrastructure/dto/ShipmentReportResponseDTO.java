package com.paquetrack.shipment.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentReportResponseDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    // ─── Conteos ─────────────────────────────────────────────────────
    private long totalCreated;
    private long totalInTransit;
    private long totalOutForDelivery;
    private long totalDelivered;
    private long totalException;
    private long totalGeneral;

    // ─── Listas por estado ───────────────────────────────────────────
    private List<ShipmentResponseDTO> created;
    private List<ShipmentResponseDTO> inTransit;
    private List<ShipmentResponseDTO> outForDelivery;
    private List<ShipmentResponseDTO> delivered;
    private List<ShipmentResponseDTO> exception;
}