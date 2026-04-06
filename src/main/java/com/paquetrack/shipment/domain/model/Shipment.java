package com.paquetrack.shipment.domain.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)  // toBuilder = true permite crear copias modificadas
@ToString
public class Shipment {
    private String id;
    private String trackingId;
    private String status;
    private String senderName;
    private String senderAddress;
    private String senderCity;
    private String recipientName;
    private String recipientAddress;
    private String recipientCity;
    private BigDecimal weightKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Métodos de dominio — modifican estado devolviendo una nueva instancia
    public Shipment markAsCreated() {
        LocalDateTime now = LocalDateTime.now();
        return this.toBuilder()
                .status("CREATED")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Shipment updateStatus(String newStatus) {
        return this.toBuilder()
                .status(newStatus)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}