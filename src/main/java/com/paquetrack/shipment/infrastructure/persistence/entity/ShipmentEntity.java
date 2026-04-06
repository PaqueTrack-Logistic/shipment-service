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
        @Index(name = "idx_shipments_tracking_id", columnList = "trackingId"),
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

    @Column(unique = true, nullable = false, length = 30)
    private String trackingId;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false, length = 200)
    private String senderName;

    @Column(length = 300)
    private String senderAddress;

    @Column(nullable = false, length = 100)
    private String senderCity;

    @Column(nullable = false, length = 200)
    private String recipientName;

    @Column(length = 300)
    private String recipientAddress;

    @Column(nullable = false, length = 100)
    private String recipientCity;


    private BigDecimal weightKg;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}