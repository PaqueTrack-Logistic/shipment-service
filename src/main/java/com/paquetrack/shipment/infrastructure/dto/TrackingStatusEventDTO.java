package com.paquetrack.shipment.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackingStatusEventDTO {
    private String shipmentId;
    private String trackingId;
    private String eventType;
    private String previousStatus;
    private String newStatus;
    private String occurredAt;
}