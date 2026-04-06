package com.paquetrack.shipment.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequestDTO {
    @NotBlank(message = "El nombre del remitente es obligatorio")
    private String senderName;
    
    private String senderAddress;
    private String senderCity;
    
    @NotBlank(message = "El nombre del destinatario es obligatorio")
    private String recipientName;
    
    private String recipientAddress;
    private String recipientCity;
    
    @Positive(message = "El peso debe ser positivo")
    private BigDecimal weightKg;
}
