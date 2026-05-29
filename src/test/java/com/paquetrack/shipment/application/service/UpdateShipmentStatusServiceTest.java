package com.paquetrack.shipment.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paquetrack.shipment.domain.exception.ShipmentNotFoundException;
import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentEventHistoryPort;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;

/**
 * Cubre la actualización de estado desde tracking: persistencia del nuevo
 * estado, registro en historial y caso de envío inexistente.
 */
@ExtendWith(MockitoExtension.class)
class UpdateShipmentStatusServiceTest {

	@Mock
	private ShipmentRepositoryPort repository;

	@Mock
	private ShipmentEventHistoryPort historyPort;

	@InjectMocks
	private UpdateShipmentStatusService service;

	@Test
	void updateStatus_savesNewStatusAndRecordsHistory() {
		Shipment existing = Shipment.builder().id("ship-1").status("CREATED").build();
		when(repository.findById("ship-1")).thenReturn(Optional.of(existing));

		service.updateStatus("ship-1", "IN_TRANSIT", "2026-05-29T10:00:00");

		ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo("IN_TRANSIT");
		verify(historyPort).save("ship-1", "CREATED", "IN_TRANSIT", "2026-05-29T10:00:00");
	}

	@Test
	void updateStatus_throwsWhenShipmentNotFound() {
		when(repository.findById("missing")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.updateStatus("missing", "IN_TRANSIT", "2026-05-29T10:00:00"))
				.isInstanceOf(ShipmentNotFoundException.class);

		verify(repository, never()).save(any());
		verify(historyPort, never()).save(any(), any(), any(), any());
	}
}
