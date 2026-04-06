package com.paquetrack.shipment.infrastructure.persistence.mapper;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.infrastructure.dto.ShipmentRequestDTO;
import com.paquetrack.shipment.infrastructure.dto.ShipmentResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEntity;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {

    public Shipment toDomain(ShipmentRequestDTO dto) {
        return Shipment.builder()
                .senderName(dto.getSenderName())
                .senderAddress(dto.getSenderAddress())
                .senderCity(dto.getSenderCity())
                .recipientName(dto.getRecipientName())
                .recipientAddress(dto.getRecipientAddress())
                .recipientCity(dto.getRecipientCity())
                .weightKg(dto.getWeightKg())
                .build();
    }

    public ShipmentResponseDTO toResponseDTO(Shipment shipment) {
        return ShipmentResponseDTO.builder()
                .id(shipment.getId())
                .trackingId(shipment.getTrackingId())
                .status(shipment.getStatus())
                .senderName(shipment.getSenderName())
                .senderAddress(shipment.getSenderAddress())
                .senderCity(shipment.getSenderCity())
                .recipientName(shipment.getRecipientName())
                .recipientAddress(shipment.getRecipientAddress())
                .recipientCity(shipment.getRecipientCity())
                .weightKg(shipment.getWeightKg())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }

    public ShipmentEntity toEntity(Shipment shipment) {
        return ShipmentEntity.builder()
                .id(shipment.getId())
                .trackingId(shipment.getTrackingId())
                .status(shipment.getStatus())
                .senderName(shipment.getSenderName())
                .senderAddress(shipment.getSenderAddress())
                .senderCity(shipment.getSenderCity())
                .recipientName(shipment.getRecipientName())
                .recipientAddress(shipment.getRecipientAddress())
                .recipientCity(shipment.getRecipientCity())
                .weightKg(shipment.getWeightKg())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }

    public Shipment toDomain(ShipmentEntity entity) {
        return Shipment.builder()
                .id(entity.getId())
                .trackingId(entity.getTrackingId())
                .status(entity.getStatus())
                .senderName(entity.getSenderName())
                .senderAddress(entity.getSenderAddress())
                .senderCity(entity.getSenderCity())
                .recipientName(entity.getRecipientName())
                .recipientAddress(entity.getRecipientAddress())
                .recipientCity(entity.getRecipientCity())
                .weightKg(entity.getWeightKg())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}