package com.paquetrack.shipment.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;

/**
 * Cubre la búsqueda de envíos por remitente y por destinatario.
 */
@ExtendWith(MockitoExtension.class)
class GetShipmentsByFilterServiceTest {

	@Mock
	private ShipmentRepositoryPort repository;

	@InjectMocks
	private GetShipmentsByFilterService service;

	@Test
	void getBySenderName_delegatesToRepository() {
		Shipment s = Shipment.builder().id("a").senderName("Ana").build();
		when(repository.findBySenderNameContaining("Ana")).thenReturn(List.of(s));

		assertThat(service.getBySenderName("Ana")).containsExactly(s);
	}

	@Test
	void getByRecipientName_delegatesToRepository() {
		Shipment s = Shipment.builder().id("b").recipientName("Luis").build();
		when(repository.findByRecipientNameContaining("Luis")).thenReturn(List.of(s));

		assertThat(service.getByRecipientName("Luis")).containsExactly(s);
	}
}
