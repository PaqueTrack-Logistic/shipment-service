package com.paquetrack.shipment.domain.exception;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(String field, String value) {
        super(String.format("Envío no encontrado con %s: '%s'", field, value));
    }
}