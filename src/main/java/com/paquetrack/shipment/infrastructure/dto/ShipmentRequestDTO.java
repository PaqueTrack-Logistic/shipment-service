package com.paquetrack.shipment.infrastructure.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequestDTO {

    @NotBlank(message = "El nombre del remitente es obligatorio")
    private String senderName;

    @NotBlank(message = "La dirección del remitente es obligatoria")
    private String senderAddress;

    @NotBlank(message = "La ciudad del remitente es obligatoria")
    private String senderCity;

    @NotBlank(message = "El nombre del destinatario es obligatorio")
    private String recipientName;

    @NotBlank(message = "La dirección del destinatario es obligatoria")
    private String recipientAddress;

    @NotBlank(message = "La ciudad del destinatario es obligatoria")
    private String recipientCity;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.001", message = "El peso debe ser mayor a 0")
    @Digits(integer = 5, fraction = 3, message = "El peso debe tener máximo 5 dígitos enteros y 3 decimales")
    private BigDecimal weightKg;
}