package com.paquetrack.shipment.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.infrastructure.persistence.entity.ShipmentEntity;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;
import com.paquetrack.shipment.infrastructure.persistence.repository.JpaShipmentRepository;

/**
 * Cubre los métodos de consulta por rango de fechas del adapter, no
 * ejercitados por el test base (conversión LocalDate -> LocalDateTime y mapeo).
 */
@ExtendWith(MockitoExtension.class)
class ShipmentRepositoryAdapterBranchesTest {

	@Mock
	private JpaShipmentRepository jpaShipmentRepository;

	@Mock
	private ShipmentMapper shipmentMapper;

	@InjectMocks
	private ShipmentRepositoryAdapter adapter;

	@Test
	void findByStatusAndDateRange_mapsEntitiesToDomain() {
		ShipmentEntity entity = ShipmentEntity.builder().id("id-1").status("CREATED").build();
		Shipment domain = Shipment.builder().id("id-1").status("CREATED").build();
		LocalDate from = LocalDate.of(2026, 4, 1);
		LocalDate to = LocalDate.of(2026, 4, 30);
		when(jpaShipmentRepository.findByStatusAndDateRange(eq("CREATED"), any(LocalDateTime.class), any(LocalDateTime.class)))
				.thenReturn(List.of(entity));
		when(shipmentMapper.toDomain(entity)).thenReturn(domain);

		List<Shipment> result = adapter.findByStatusAndDateRange("CREATED", from, to);

		assertThat(result).containsExactly(domain);
		verify(jpaShipmentRepository).findByStatusAndDateRange(eq("CREATED"), any(LocalDateTime.class), any(LocalDateTime.class));
	}

	@Test
	void findByDateRange_mapsEntitiesToDomain() {
		ShipmentEntity entity = ShipmentEntity.builder().id("id-2").status("IN_TRANSIT").build();
		Shipment domain = Shipment.builder().id("id-2").status("IN_TRANSIT").build();
		LocalDate from = LocalDate.of(2026, 4, 1);
		LocalDate to = LocalDate.of(2026, 4, 30);
		when(jpaShipmentRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
				.thenReturn(List.of(entity));
		when(shipmentMapper.toDomain(entity)).thenReturn(domain);

		List<Shipment> result = adapter.findByDateRange(from, to);

		assertThat(result).containsExactly(domain);
	}
}
