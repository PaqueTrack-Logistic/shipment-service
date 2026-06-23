package com.paquetrack.shipment.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentEventPublisherPort;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;

/**
 * Cubre las validaciones de auditoría de CreateShipmentService: el envío debe
 * traer createdBy y createdByRole, si no se rechaza antes de persistir.
 */
@ExtendWith(MockitoExtension.class)
class CreateShipmentServiceBranchesTest {

	@Mock
	private ShipmentRepositoryPort repository;

	@Mock
	private ShipmentEventPublisherPort publisher;

	@InjectMocks
	private CreateShipmentService service;

	@Test
	void createShipment_throwsWhenCreatedByMissing() {
		Shipment shipment = Shipment.builder()
				.senderName("Ana").createdByRole("ROLE_ADMIN").build(); // createdBy == null

		assertThatThrownBy(() -> service.createShipment(shipment))
				.isInstanceOf(IllegalStateException.class);

		verify(repository, never()).save(any());
		verify(publisher, never()).publishShipmentCreated(any());
	}

	@Test
	void createShipment_throwsWhenRoleMissing() {
		Shipment shipment = Shipment.builder()
				.senderName("Ana").createdBy("operador@logistics.com").build(); // createdByRole == null

		assertThatThrownBy(() -> service.createShipment(shipment))
				.isInstanceOf(IllegalStateException.class);

		verify(repository, never()).save(any());
		verify(publisher, never()).publishShipmentCreated(any());
	}
}
