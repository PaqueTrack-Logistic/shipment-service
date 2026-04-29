package com.paquetrack.shipment.infrastructure.persistence.entity;

import java.time.LocalDateTime;

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
@Table(name = "shipment_event_history", indexes = {
        @Index(name = "idx_event_history_shipment_id", columnList = "shipment_id"),
        @Index(name = "idx_event_history_recorded_at", columnList = "recorded_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEventHistoryEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "shipment_id", nullable = false, length = 36)
    private String shipmentId;

    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    @Column(name = "new_status", nullable = false, length = 30)
    private String newStatus;

    @Column(name = "occurred_at", nullable = false, length = 50)
    private String occurredAt;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}