package com.paquetrack.shipment.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.paquetrack.shipment.domain.port.out.ShipmentEventHistoryPort;
import com.paquetrack.shipment.infrastructure.dto.ShipmentEventHistoryResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEventHistoryEntity;
import com.paquetrack.shipment.infrastructure.persistence.repository.JpaShipmentEventHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventHistoryAdapter implements ShipmentEventHistoryPort {

        private final JpaShipmentEventHistoryRepository repository;

        @Override
        public void save(String shipmentId, String previousStatus,
                        String newStatus, String occurredAt) {

                ShipmentEventHistoryEntity entity = ShipmentEventHistoryEntity.builder()
                                .id(UUID.randomUUID().toString())
                                .shipmentId(shipmentId)
                                .previousStatus(previousStatus)
                                .newStatus(newStatus)
                                .occurredAt(occurredAt)
                                .recordedAt(LocalDateTime.now())
                                .build();

                repository.save(entity);
                log.debug("Evento guardado en historial — shipmentId: {} | {} → {}",
                                shipmentId, previousStatus, newStatus);
        }

        @Override
        public List<ShipmentEventHistoryResponseDTO> findByShipmentId(String shipmentId) {
                return repository.findByShipmentIdOrderByRecordedAtDesc(shipmentId)
                                .stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());
        }

        private ShipmentEventHistoryResponseDTO toDTO(ShipmentEventHistoryEntity entity) {
                return ShipmentEventHistoryResponseDTO.builder()
                                .id(entity.getId())
                                .shipmentId(entity.getShipmentId())
                                .previousStatus(entity.getPreviousStatus())
                                .newStatus(entity.getNewStatus())
                                .occurredAt(entity.getOccurredAt())
                                .recordedAt(entity.getRecordedAt())
                                .build();
        }
}