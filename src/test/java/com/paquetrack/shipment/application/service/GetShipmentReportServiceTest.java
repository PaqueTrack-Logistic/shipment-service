package com.paquetrack.shipment.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.out.ShipmentRepositoryPort;
import com.paquetrack.shipment.infrastructure.dto.ShipmentReportResponseDTO;
import com.paquetrack.shipment.infrastructure.dto.ShipmentResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;

/**
 * Cubre la generación del reporte agregado por estado (consulta por cada uno
 * de los 5 estados y totales).
 */
@ExtendWith(MockitoExtension.class)
class GetShipmentReportServiceTest {

	@Mock
	private ShipmentRepositoryPort repository;

	@Mock
	private ShipmentMapper shipmentMapper;

	@InjectMocks
	private GetShipmentReportService service;

	@Test
	void getReport_aggregatesCountsByStatus() {
		LocalDate from = LocalDate.of(2026, 4, 1);
		LocalDate to = LocalDate.of(2026, 4, 30);
		Shipment s = Shipment.builder().id("x").build();

		when(repository.findByStatusAndDateRange(eq("CREATED"), any(), any())).thenReturn(List.of(s, s));
		when(repository.findByStatusAndDateRange(eq("IN_TRANSIT"), any(), any())).thenReturn(List.of(s));
		when(repository.findByStatusAndDateRange(eq("OUT_FOR_DELIVERY"), any(), any())).thenReturn(List.of());
		when(repository.findByStatusAndDateRange(eq("DELIVERED"), any(), any())).thenReturn(List.of());
		when(repository.findByStatusAndDateRange(eq("EXCEPTION"), any(), any())).thenReturn(List.of());
		when(shipmentMapper.toResponseDTO(any())).thenReturn(mock(ShipmentResponseDTO.class));

		ShipmentReportResponseDTO report = service.getReport(from, to);

		assertThat(report.getFrom()).isEqualTo(from);
		assertThat(report.getTo()).isEqualTo(to);
		assertThat(report.getTotalCreated()).isEqualTo(2);
		assertThat(report.getTotalInTransit()).isEqualTo(1);
		assertThat(report.getTotalOutForDelivery()).isZero();
		assertThat(report.getTotalDelivered()).isZero();
		assertThat(report.getTotalException()).isZero();
		assertThat(report.getTotalGeneral()).isEqualTo(3);
		assertThat(report.getCreated()).hasSize(2);
		assertThat(report.getInTransit()).hasSize(1);
		assertThat(report.getDelivered()).isEmpty();
	}
}
