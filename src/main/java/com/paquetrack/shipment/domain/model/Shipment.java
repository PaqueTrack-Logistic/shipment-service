package com.paquetrack.shipment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
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

    // Auditoría
    private String createdBy;
    private String createdByRole;

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

    // Establecer el creador durante la creación
    public Shipment withCreator(String username, String role) {
        return this.toBuilder()
                .createdBy(username)
                .createdByRole(role)
                .build();
    }

    // Helper para verificar permisos
    public boolean wasCreatedBy(String username) {
        return this.createdBy != null && this.createdBy.equals(username);
    }

    // Verificar si el creador tiene rol específico
    public boolean wasCreatedByRole(String role) {
        return this.createdByRole != null && this.createdByRole.equals(role);
    }
}