package com.paquetrack.shipment.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_shipments_tracking_id", columnList = "tracking_id"),
        @Index(name = "idx_shipments_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "tracking_id", unique = true, nullable = false, length = 30)
    private String trackingId;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "sender_name", nullable = false, length = 200)
    private String senderName;

    @Column(name = "sender_address", length = 300)
    private String senderAddress;

    @Column(name = "sender_city", nullable = false, length = 100)
    private String senderCity;

    @Column(name = "recipient_name", nullable = false, length = 200)
    private String recipientName;

    @Column(name = "recipient_address", length = 300)
    private String recipientAddress;

    @Column(name = "recipient_city", nullable = false, length = 100)
    private String recipientCity;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}