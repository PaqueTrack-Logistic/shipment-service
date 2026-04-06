package com.paquetrack.shipment.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paquetrack.shipment.application.service.CreateShipmentService;
import com.paquetrack.shipment.application.service.GetShipmentByTrackingService;
import com.paquetrack.shipment.application.service.GetShipmentService;
import com.paquetrack.shipment.domain.port.in.CreateShipmentUseCase;
import com.paquetrack.shipment.domain.port.in.GetShipmentByTrackingUseCase;
import com.paquetrack.shipment.domain.port.in.GetShipmentUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentEventPublisherPort;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;

@Configuration
public class BeanConfig {

    @Bean
    public CreateShipmentUseCase createShipmentUseCase(
            ShipmentRepositoryPort repo,
            ShipmentEventPublisherPort publisher) {
        return new CreateShipmentService(repo, publisher);
    }

    @Bean
    public GetShipmentUseCase getShipmentUseCase(
            ShipmentRepositoryPort repo) {
        return new GetShipmentService(repo);
    }

    @Bean
    public GetShipmentByTrackingUseCase getShipmentByTrackingUseCase(
            ShipmentRepositoryPort repo) {
        return new GetShipmentByTrackingService(repo);
    }
}