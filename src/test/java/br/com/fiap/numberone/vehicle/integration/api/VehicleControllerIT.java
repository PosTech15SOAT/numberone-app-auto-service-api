package br.com.fiap.numberone.vehicle.integration.api;

import br.com.fiap.numberone.shared.api.exception.GlobalExceptionHandler;
import br.com.fiap.numberone.shared.security.infrastructure.repositories.AdminUserRepository;
import br.com.fiap.numberone.shared.security.infrastructure.token.JwtService;
import br.com.fiap.numberone.vehicle.api.controllers.VehicleController;
import br.com.fiap.numberone.vehicle.api.exceptions.VehicleExceptionHandler;
import br.com.fiap.numberone.vehicle.api.mappers.VehicleApiMapper;
import br.com.fiap.numberone.vehicle.application.services.VehicleService;
import br.com.fiap.numberone.vehicle.domain.entities.Vehicle;
import br.com.fiap.numberone.vehicle.domain.exceptions.VehicleCustomerNotFoundException;
import br.com.fiap.numberone.vehicle.domain.exceptions.VehicleLicensePlateAlreadyExistsException;
import br.com.fiap.numberone.vehicle.domain.exceptions.VehicleNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        VehicleApiMapper.class,
        VehicleExceptionHandler.class,
        GlobalExceptionHandler.class
})
class VehicleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AdminUserRepository adminUserRepository;

    @Test
    void shouldCreateVehicle() throws Exception {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(vehicleService.create(any(Vehicle.class))).thenReturn(vehicle(vehicleId, "ABC1D23", customerId));

        // Act & Assert
        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "AAA-1234",
                                  "marca": "Fiat",
                                  "modelo": "Argo",
                                  "ano": 2023,
                                  "idCliente": "%s"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/admin/veiculos/" + vehicleId))
                .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                .andExpect(jsonPath("$.placa").value("ABC1D23"))
                .andExpect(jsonPath("$.marca").value("Fiat"))
                .andExpect(jsonPath("$.modelo").value("Argo"))
                .andExpect(jsonPath("$.ano").value(2023))
                .andExpect(jsonPath("$.idCliente").value(customerId.toString()))
                .andExpect(jsonPath("$.criadoEm").exists());
    }

    @Test
    void shouldCreateVehicleMercosul() throws Exception {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(vehicleService.create(any(Vehicle.class))).thenReturn(vehicle(vehicleId, "ABC1D23", customerId));

        // Act & Assert
        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "abc1d23",
                                  "marca": "Fiat",
                                  "modelo": "Argo",
                                  "ano": 2023,
                                  "idCliente": "%s"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/admin/veiculos/" + vehicleId))
                .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                .andExpect(jsonPath("$.placa").value("ABC1D23"))
                .andExpect(jsonPath("$.marca").value("Fiat"))
                .andExpect(jsonPath("$.modelo").value("Argo"))
                .andExpect(jsonPath("$.ano").value(2023))
                .andExpect(jsonPath("$.idCliente").value(customerId.toString()))
                .andExpect(jsonPath("$.criadoEm").exists());
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingInvalidVehicle() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "placa": "",
                  "marca": "",
                  "modelo": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());

        verify(vehicleService, never()).create(any(Vehicle.class));
    }


    @Test
    void shouldReturnValidationErrorWhenLicensePlateFormatIsInvalid() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "AB12345",
                                  "marca": "Fiat",
                                  "modelo": "Argo",
                                  "ano": 2023,
                                  "idCliente": "%s"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());

        verify(vehicleService, never()).create(any(Vehicle.class));
    }

    @Test
    void shouldReturnBusinessErrorWhenLicensePlateAlreadyExists() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(vehicleService.create(any(Vehicle.class)))
                .thenThrow(new VehicleLicensePlateAlreadyExistsException("Ja existe um veiculo com a placa informada"));

        // Act & Assert
        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "ABC1D23",
                                  "marca": "Fiat",
                                  "modelo": "Argo",
                                  "ano": 2023,
                                  "idCliente": "%s"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Ja existe um veiculo com a placa informada"));
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(vehicleService.create(any(Vehicle.class)))
                .thenThrow(new VehicleCustomerNotFoundException("Cliente nao encontrado para o id: " + customerId));

        // Act & Assert
        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "ABC1D23",
                                  "marca": "Fiat",
                                  "modelo": "Argo",
                                  "ano": 2023,
                                  "idCliente": "%s"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cliente nao encontrado para o id: " + customerId));
    }

    @Test
    void shouldReturnVehicleById() throws Exception {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(vehicleService.findById(vehicleId)).thenReturn(vehicle(vehicleId, "ABC1D23", customerId));

        // Act & Assert
        mockMvc.perform(get("/api/admin/veiculos/{id}", vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                .andExpect(jsonPath("$.placa").value("ABC1D23"));
    }

    @Test
    void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        when(vehicleService.findById(vehicleId))
                .thenThrow(new VehicleNotFoundException("Veiculo nao encontrado para o id: " + vehicleId));

        // Act & Assert
        mockMvc.perform(get("/api/admin/veiculos/{id}", vehicleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Veiculo nao encontrado para o id: " + vehicleId));
    }

    @Test
    void shouldReturnAllVehicles() throws Exception {
        // Arrange
        Vehicle firstVehicle = vehicle(UUID.randomUUID(), "ABC1D23", UUID.randomUUID());
        Vehicle secondVehicle = vehicle(UUID.randomUUID(), "DEF2G34", UUID.randomUUID());
        when(vehicleService.findAll()).thenReturn(List.of(firstVehicle, secondVehicle));

        // Act & Assert
        mockMvc.perform(get("/api/admin/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placa").value("ABC1D23"))
                .andExpect(jsonPath("$[1].placa").value("DEF2G34"));
    }

    @Test
    void shouldUpdateVehicle() throws Exception {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(vehicleService.update(any(UUID.class), any(Vehicle.class))).thenReturn(vehicle(vehicleId, "ABC1D23", customerId));

        // Act & Assert
        mockMvc.perform(put("/api/admin/veiculos/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "placa": "ABC1D23",
                                  "marca": "Fiat",
                                  "modelo": "Argo",
                                  "ano": 2023,
                                  "idCliente": "%s"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                .andExpect(jsonPath("$.placa").value("ABC1D23"));
    }

    @Test
    void shouldDeleteVehicle() throws Exception {
        // Arrange
        UUID vehicleId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/admin/veiculos/{id}", vehicleId))
                .andExpect(status().isNoContent());

        verify(vehicleService).delete(vehicleId);
    }

    private static Vehicle vehicle(UUID id, String licensePlate, UUID customerId) {
        return Vehicle.builder()
                .id(id)
                .licensePlate(licensePlate)
                .brand("Fiat")
                .model("Argo")
                .year(2023)
                .customerId(customerId)
                .createdAt(LocalDateTime.of(2026, 4, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 2, 10, 0))
                .build();
    }
}
