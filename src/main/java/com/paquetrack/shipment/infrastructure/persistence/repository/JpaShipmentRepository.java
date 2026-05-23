package com.paquetrack.shipment.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEntity;

public interface JpaShipmentRepository extends JpaRepository<ShipmentEntity, String> {
    Optional<ShipmentEntity> findByTrackingId(String trackingId);

    // Case insensitive para remitente y destinatario
    List<ShipmentEntity> findBySenderNameContainingIgnoreCase(String senderName);

    List<ShipmentEntity> findByRecipientNameContainingIgnoreCase(String recipientName);

    
    @Query("SELECT s FROM ShipmentEntity s WHERE s.status = :status " +
            "AND s.createdAt >= :from AND s.createdAt <= :to")
    List<ShipmentEntity> findByStatusAndDateRange(
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    
    @Query("SELECT s FROM ShipmentEntity s " +
            "WHERE s.createdAt >= :from AND s.createdAt <= :to")
    List<ShipmentEntity> findByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

}