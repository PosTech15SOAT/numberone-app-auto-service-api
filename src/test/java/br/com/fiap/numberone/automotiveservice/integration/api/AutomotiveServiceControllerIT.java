package br.com.fiap.numberone.automotiveservice.integration.api;

import br.com.fiap.numberone.automotiveservice.api.controllers.AutomotiveServiceController;
import br.com.fiap.numberone.automotiveservice.api.exceptions.AutomotiveServiceExceptionHandler;
import br.com.fiap.numberone.automotiveservice.api.mappers.AutomotiveServiceApiMapper;
import br.com.fiap.numberone.automotiveservice.application.services.AutomotiveServiceService;
import br.com.fiap.numberone.automotiveservice.domain.entities.AutomotiveService;
import br.com.fiap.numberone.automotiveservice.domain.exceptions.AutoServiceNotFoundException;
import br.com.fiap.numberone.automotiveservice.domain.exceptions.AutomotiveServiceBusinessException;
import br.com.fiap.numberone.shared.api.exception.GlobalExceptionHandler;
import br.com.fiap.numberone.shared.security.application.gateways.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static br.com.fiap.numberone.automotiveservice.support.AutomotiveServiceTestFactory.automotiveService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AutomotiveServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        AutomotiveServiceControllerIT.MapperTestConfig.class,
        AutomotiveServiceExceptionHandler.class,
        GlobalExceptionHandler.class
})
class AutomotiveServiceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutomotiveServiceService automotiveServiceService;

    @MockitoBean
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Test
    void shouldCreateAutomotiveService() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(automotiveServiceService.create(any(AutomotiveService.class)))
                .thenReturn(automotiveService(id, "REV-001", true));

        // When / Then
        mockMvc.perform(post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "REV-001",
                                  "nome": "Revisao completa",
                                  "descricao": "Inspecao preventiva completa",
                                  "tipoServico": "REVISAO",
                                  "valorBase": 350.00,
                                  "tempoEstimadoMinutos": 120
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/admin/servicos/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value("REV-001"))
                .andExpect(jsonPath("$.name").value("Revisao completa"))
                .andExpect(jsonPath("$.description").value("Inspecao preventiva completa"))
                .andExpect(jsonPath("$.serviceType").value("REVISAO"))
                .andExpect(jsonPath("$.baseValue").value(350.00))
                .andExpect(jsonPath("$.estimatedTimeMinutes").value(120))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingInvalidAutomotiveService() throws Exception {
        // Given
        String requestBody = """
                {
                  "codigo": "",
                  "nome": "",
                  "descricao": "",
                  "valorBase": 0,
                  "tempoEstimadoMinutos": 0
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").isArray());

        verify(automotiveServiceService, never()).create(any());
    }

    @Test
    void shouldReturnValidationErrorWhenServiceTypeIsInvalid() throws Exception {
        // Given / When / Then
        mockMvc.perform(post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "REV-001",
                                  "nome": "Revisao completa",
                                  "descricao": "Inspecao preventiva completa",
                                  "tipoServico": "INVALIDO",
                                  "valorBase": 350.00,
                                  "tempoEstimadoMinutos": 120
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("tipoServico")));
    }

    @Test
    void shouldReturnBusinessErrorWhenCodeAlreadyExists() throws Exception {
        // Given
        when(automotiveServiceService.create(any(AutomotiveService.class)))
                .thenThrow(new AutomotiveServiceBusinessException("Já existe um serviço automotivo com o código informado"));

        // When / Then
        mockMvc.perform(post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "REV-001",
                                  "nome": "Revisao completa",
                                  "descricao": "Inspecao preventiva completa",
                                  "tipoServico": "REVISAO",
                                  "valorBase": 350.00,
                                  "tempoEstimadoMinutos": 120
                                }
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Já existe um serviço automotivo com o código informado"));
    }

    @Test
    void shouldReturnAutomotiveServiceById() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(automotiveServiceService.findById(id)).thenReturn(automotiveService(id, "REV-001", true));

        // When / Then
        mockMvc.perform(get("/api/admin/servicos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value("REV-001"));
    }

    @Test
    void shouldReturnNotFoundWhenAutomotiveServiceDoesNotExist() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(automotiveServiceService.findById(id))
                .thenThrow(new AutoServiceNotFoundException("Serviço automotivo não encontrado"));

        // When / Then
        mockMvc.perform(get("/api/admin/servicos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Serviço automotivo não encontrado"));
    }

    @Test
    void shouldReturnAllAutomotiveServices() throws Exception {
        // Given
        when(automotiveServiceService.findAll()).thenReturn(List.of(
                automotiveService(UUID.randomUUID(), "REV-001", true),
                automotiveService(UUID.randomUUID(), "ALI-001", true)
        ));

        // When / Then
        mockMvc.perform(get("/api/admin/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("REV-001"))
                .andExpect(jsonPath("$[1].code").value("ALI-001"));
    }

    @Test
    void shouldUpdateAutomotiveService() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(automotiveServiceService.update(any(UUID.class), any(AutomotiveService.class)))
                .thenReturn(automotiveService(id, "ALI-001", true));

        // When / Then
        mockMvc.perform(put("/api/admin/servicos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "ALI-001",
                                  "nome": "Alinhamento",
                                  "descricao": "Alinhamento e balanceamento",
                                  "tipoServico": "ALINHAMENTO_BALANCEAMENTO",
                                  "valorBase": 180.00,
                                  "tempoEstimadoMinutos": 60
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value("ALI-001"));
    }

    @Test
    void shouldInactivateAutomotiveService() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When / Then
        mockMvc.perform(patch("/api/admin/servicos/{id}/inativar", id))
                .andExpect(status().isNoContent());

        verify(automotiveServiceService).inactivate(id);
    }

    @Test
    void shouldActivateAutomotiveService() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When / Then
        mockMvc.perform(patch("/api/admin/servicos/{id}/ativar", id))
                .andExpect(status().isNoContent());

        verify(automotiveServiceService).activate(id);
    }

    @TestConfiguration
    static class MapperTestConfig {

        @Bean
        AutomotiveServiceApiMapper automotiveServiceApiMapper() {
            return Mappers.getMapper(AutomotiveServiceApiMapper.class);
        }
    }
}
