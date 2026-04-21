// infrastructure/config/JwtProperties.java - Versión con @Value
package com.paquetrack.shipment.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class JwtProperties {

    @Value("${jwt.secret}")
    private String secret;
}