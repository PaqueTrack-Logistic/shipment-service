package com.paquetrack.shipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ShipmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShipmentApplication.class, args);
    }
}