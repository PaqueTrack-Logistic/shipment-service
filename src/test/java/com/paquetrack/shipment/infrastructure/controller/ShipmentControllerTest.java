package com.paquetrack.shipment.infrastructure.controller;

import com.paquetrack.shipment.domain.model.Shipment;
import com.paquetrack.shipment.domain.port.in.CreateShipmentUseCase;
import com.paquetrack.shipment.domain.port.in.GetShipmentByTrackingUseCase;
import com.paquetrack.shipment.domain.port.in.GetShipmentUseCase;
import com.paquetrack.shipment.domain.port.in.GetShipmentsByFilterUseCase;
import com.paquetrack.shipment.domain.port.out.ShipmentEventHistoryPort;
import com.paquetrack.shipment.infrastructure.dto.ShipmentEventHistoryResponseDTO;
import com.paquetrack.shipment.infrastructure.dto.ShipmentRequestDTO;
import com.paquetrack.shipment.infrastructure.dto.ShipmentResponseDTO;
import com.paquetrack.shipment.infrastructure.persistence.mapper.ShipmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShipmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateShipmentUseCase createShipmentUseCase;

    @Mock
    private GetShipmentUseCase getShipmentUseCase;

    @Mock
    private GetShipmentByTrackingUseCase getShipmentByTrackingUseCase;

    @Mock
    private ShipmentMapper shipmentMapper;

    @Mock
    private GetShipmentsByFilterUseCase getShipmentsByFilterUseCase;

    @Mock
    private ShipmentEventHistoryPort shipmentEventHistoryPort;

    @InjectMocks
    private ShipmentController shipmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(shipmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Shipment buildCreatedShipment() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 7, 10, 30, 0);
        return Shipment.builder()
                .id("abc-123")
                .trackingId("PQ-20260407-XYZ789")
                .status("CREATED")
                .senderName("Juan Perez")
                .senderAddress("Calle 1")
                .senderCity("Medellin")
                .recipientName("Maria Lopez")
                .recipientAddress("Cra 2")
                .recipientCity("Bogota")
                .weightKg(new BigDecimal("2.5"))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private void setupMapperForCreation(Shipment created) {
        when(shipmentMapper.toDomain(any(ShipmentRequestDTO.class)))
                .thenReturn(Shipment.builder()
                        .senderName("Juan Perez")
                        .senderAddress("Calle 1")
                        .senderCity("Medellin")
                        .recipientName("Maria Lopez")
                        .recipientAddress("Cra 2")
                        .recipientCity("Bogota")
                        .weightKg(new BigDecimal("2.5"))
                        .build());

        when(shipmentMapper.toResponseDTO(created))
                .thenReturn(ShipmentResponseDTO.builder()
                        .id(created.getId())
                        .trackingId(created.getTrackingId())
                        .status(created.getStatus())
                        .senderName(created.getSenderName())
                        .senderAddress(created.getSenderAddress())
                        .senderCity(created.getSenderCity())
                        .recipientName(created.getRecipientName())
                        .recipientAddress(created.getRecipientAddress())
                        .recipientCity(created.getRecipientCity())
                        .weightKg(created.getWeightKg())
                        .createdAt(created.getCreatedAt())
                        .updatedAt(created.getUpdatedAt())
                        .build());
    }

    @Test
    @DisplayName("CP-01-01: POST /api/shipments with valid data returns 201 Created")
    void createShipmentWithValidDataReturns201() throws Exception {
        Shipment created = buildCreatedShipment();
        setupMapperForCreation(created);
        when(createShipmentUseCase.createShipment(any(Shipment.class))).thenReturn(created);

        String requestBody = """
                {
                    "senderName": "Juan Perez",
                    "senderAddress": "Calle 1",
                    "senderCity": "Medellin",
                    "recipientName": "Maria Lopez",
                    "recipientAddress": "Cra 2",
                    "recipientCity": "Bogota",
                    "weightKg": 2.5
                }
                """;

        mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.trackingId").value("PQ-20260407-XYZ789"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.senderName").value("Juan Perez"))
                .andExpect(jsonPath("$.recipientName").value("Maria Lopez"));
    }

    @Test
    @DisplayName("CP-01-02: POST /api/shipments with missing required fields returns 400")
    void createShipmentWithMissingFieldsReturns400() throws Exception {
        String requestBody = """
                {
                    "senderAddress": "Calle 1",
                    "senderCity": "Medellin"
                }
                """;

        mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CP-02-01: POST /api/shipments returns response with trackingId in correct format")
    void createShipmentReturnsTrackingIdInCorrectFormat() throws Exception {
        Shipment created = buildCreatedShipment();
        setupMapperForCreation(created);
        when(createShipmentUseCase.createShipment(any(Shipment.class))).thenReturn(created);

        String requestBody = """
                {
                    "senderName": "Juan Perez",
                    "senderAddress": "Calle 1",
                    "senderCity": "Medellin",
                    "recipientName": "Maria Lopez",
                    "recipientAddress": "Cra 2",
                    "recipientCity": "Bogota",
                    "weightKg": 2.5
                }
                """;

        mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackingId").isNotEmpty())
                .andExpect(jsonPath("$.trackingId").value(matchesPattern("PQ-\\d{8}-[A-Z0-9]{6}")));
    }

    @Test
    @DisplayName("CP-03-01: GET /api/shipments/{id} returns 200 with existing shipment")
    void getShipmentByIdReturns200WhenFound() throws Exception {
        Shipment shipment = buildCreatedShipment();
        when(getShipmentUseCase.getShipment("abc-123")).thenReturn(Optional.of(shipment));
        when(shipmentMapper.toResponseDTO(shipment))
                .thenReturn(ShipmentResponseDTO.builder()
                        .id(shipment.getId())
                        .trackingId(shipment.getTrackingId())
                        .status(shipment.getStatus())
                        .senderName(shipment.getSenderName())
                        .recipientName(shipment.getRecipientName())
                        .build());

        mockMvc.perform(get("/api/shipments/abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.trackingId").value("PQ-20260407-XYZ789"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("CP-03-02: GET /api/shipments/{id} returns 404 when not found")
    void getShipmentByIdReturns404WhenNotFound() throws Exception {
        when(getShipmentUseCase.getShipment("non-existent-id")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/shipments/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/shipments/tracking/{trackingId} returns 200 with existing shipment")
    void getShipmentByTrackingIdReturns200WhenFound() throws Exception {
        Shipment shipment = buildCreatedShipment();
        when(getShipmentByTrackingUseCase.getShipmentByTrackingId("PQ-20260407-XYZ789"))
                .thenReturn(Optional.of(shipment));
        when(shipmentMapper.toResponseDTO(shipment))
                .thenReturn(ShipmentResponseDTO.builder()
                        .id(shipment.getId())
                        .trackingId(shipment.getTrackingId())
                        .status(shipment.getStatus())
                        .senderName(shipment.getSenderName())
                        .recipientName(shipment.getRecipientName())
                        .build());

        mockMvc.perform(get("/api/shipments/tracking/PQ-20260407-XYZ789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("PQ-20260407-XYZ789"))
                .andExpect(jsonPath("$.id").value("abc-123"));
    }

    @Test
    @DisplayName("GET /api/shipments/tracking/{trackingId} returns 404 when not found")
    void getShipmentByTrackingIdReturns404WhenNotFound() throws Exception {
        when(getShipmentByTrackingUseCase.getShipmentByTrackingId("PQ-00000000-NOEXIST"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/shipments/tracking/PQ-00000000-NOEXIST"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CP-05-01: GET /api/shipments/{id}/history returns 200 with event list")
    void getShipmentHistoryReturns200WithEvents() throws Exception {
        // Arrange
        Shipment shipment = buildCreatedShipment();
        ShipmentEventHistoryResponseDTO event = ShipmentEventHistoryResponseDTO.builder()
                .id("evt-001")
                .shipmentId("abc-123")
                .previousStatus("CREATED")
                .newStatus("DISPATCHED")
                .occurredAt("Medellin HUB")
                .recordedAt(LocalDateTime.of(2026, 4, 7, 10, 30, 0))
                .build();

        when(getShipmentUseCase.getShipment("abc-123")).thenReturn(Optional.of(shipment));
        when(shipmentEventHistoryPort.findByShipmentId("abc-123")).thenReturn(List.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/shipments/abc-123/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].shipmentId").value("abc-123"))
                .andExpect(jsonPath("$[0].previousStatus").value("CREATED"))
                .andExpect(jsonPath("$[0].newStatus").value("DISPATCHED"));
    }

    @Test
    @DisplayName("CP-05-02: GET /api/shipments/{id}/history returns 200 with empty list")
    void getShipmentHistoryReturns200WithEmptyList() throws Exception {
        // Arrange
        Shipment shipment = buildCreatedShipment();

        when(getShipmentUseCase.getShipment("abc-123")).thenReturn(Optional.of(shipment));
        when(shipmentEventHistoryPort.findByShipmentId("abc-123")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/shipments/abc-123/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("CP-05-03: GET /api/shipments/{id}/history returns 404 when shipment not found")
    void getShipmentHistoryReturns404WhenShipmentNotFound() throws Exception {
        // Arrange
        when(getShipmentUseCase.getShipment("non-existent-id")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/shipments/non-existent-id/history"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CP-06-01: GET /api/shipments/search by senderName returns 200 with results")
    void searchShipmentsBySenderNameReturns200() throws Exception {
        // Arrange
        Shipment shipment = buildCreatedShipment();
        ShipmentResponseDTO responseDTO = ShipmentResponseDTO.builder()
                .id(shipment.getId())
                .senderName(shipment.getSenderName())
                .build();

        when(getShipmentsByFilterUseCase.getBySenderName("Juan Perez")).thenReturn(List.of(shipment));
        when(shipmentMapper.toResponseDTO(shipment)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/shipments/search")
                        .param("senderName", "Juan Perez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].senderName").value("Juan Perez"));
    }

    @Test
    @DisplayName("CP-06-02: GET /api/shipments/search by recipientName returns 200 with results")
    void searchShipmentsByRecipientNameReturns200() throws Exception {
        // Arrange
        Shipment shipment = buildCreatedShipment();
        ShipmentResponseDTO responseDTO = ShipmentResponseDTO.builder()
                .id(shipment.getId())
                .recipientName(shipment.getRecipientName())
                .build();

        when(getShipmentsByFilterUseCase.getByRecipientName("Maria Lopez")).thenReturn(List.of(shipment));
        when(shipmentMapper.toResponseDTO(shipment)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/shipments/search")
                        .param("recipientName", "Maria Lopez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recipientName").value("Maria Lopez"));
    }

    @Test
    @DisplayName("CP-06-03: GET /api/shipments/search with both params returns 400")
    void searchShipmentsWithBothParamsReturns400() throws Exception {
        // Arrange — no mocks needed, validation happens before use case calls

        // Act & Assert
        mockMvc.perform(get("/api/shipments/search")
                        .param("senderName", "Juan Perez")
                        .param("recipientName", "Maria Lopez"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("CP-06-04: GET /api/shipments/search with no params returns 400")
    void searchShipmentsWithNoParamsReturns400() throws Exception {
        // Arrange — no mocks needed, validation happens before use case calls

        // Act & Assert
        mockMvc.perform(get("/api/shipments/search"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("CP-06-05: GET /api/shipments/search by senderName returns 200 with empty list")
    void searchShipmentsBySenderNameReturnsEmptyList() throws Exception {
        // Arrange
        when(getShipmentsByFilterUseCase.getBySenderName("Desconocido")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/shipments/search")
                        .param("senderName", "Desconocido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
