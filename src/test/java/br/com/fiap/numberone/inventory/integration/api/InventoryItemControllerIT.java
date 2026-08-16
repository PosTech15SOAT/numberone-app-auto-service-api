package br.com.fiap.numberone.inventory.integration.api;

import br.com.fiap.numberone.inventory.api.controllers.InventoryItemController;
import br.com.fiap.numberone.inventory.api.exceptions.InventoryItemExceptionHandler;
import br.com.fiap.numberone.inventory.api.mappers.InventoryItemApiMapper;
import br.com.fiap.numberone.inventory.application.services.InventoryItemService;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemBusinessException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemNotFoundException;
import br.com.fiap.numberone.shared.api.exception.GlobalExceptionHandler;
import br.com.fiap.numberone.shared.security.application.gateways.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.inventoryItem;
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

@WebMvcTest(controllers = InventoryItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        InventoryItemControllerIT.MapperTestConfig.class,
        InventoryItemExceptionHandler.class,
        GlobalExceptionHandler.class
})
class InventoryItemControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryItemService inventoryItemService;

    @MockitoBean
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Test
    void shouldCreateInventoryItem() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(inventoryItemService.create(any(InventoryItem.class)))
                .thenReturn(inventoryItem(id, "OLEO-001", true, 10));

        // When / Then
        mockMvc.perform(post("/api/admin/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validItemRequest()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/admin/itens/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.codigo").value("OLEO-001"))
                .andExpect(jsonPath("$.nome").value("Oleo de motor"))
                .andExpect(jsonPath("$.tipoItem").value("PECA"))
                .andExpect(jsonPath("$.unidadeMedida").value("UNIDADE"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(10))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingInvalidInventoryItem() throws Exception {
        // Given / When / Then
        mockMvc.perform(post("/api/admin/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "OLEO-001",
                                  "nome": "",
                                  "tipoItem": null,
                                  "unidadeMedida": null,
                                  "custoUnitario": 0,
                                  "precoVenda": 0,
                                  "quantidadeEstoque": -1,
                                  "estoqueMinimo": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").isArray());

        verify(inventoryItemService, never()).create(any());
    }

    @Test
    void shouldReturnValidationErrorWhenItemTypeIsInvalid() throws Exception {
        // Given / When / Then
        mockMvc.perform(post("/api/admin/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "OLEO-001",
                                  "nome": "Oleo de motor",
                                  "descricao": "Oleo sintetico 5W30",
                                  "tipoItem": "INVALIDO",
                                  "unidadeMedida": "UNIDADE",
                                  "custoUnitario": 45.90,
                                  "precoVenda": 79.90,
                                  "quantidadeEstoque": 10,
                                  "estoqueMinimo": 3
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("tipoItem")));
    }

    @Test
    void shouldReturnBusinessErrorWhenCodeAlreadyExists() throws Exception {
        // Given
        when(inventoryItemService.create(any(InventoryItem.class)))
                .thenThrow(new InventoryItemBusinessException("Já existe um item de estoque com o código informado"));

        // When / Then
        mockMvc.perform(post("/api/admin/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validItemRequest()))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Já existe um item de estoque com o código informado"));
    }

    @Test
    void shouldReturnInventoryItemById() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(inventoryItemService.findById(id)).thenReturn(inventoryItem(id, "OLEO-001", true, 10));

        // When / Then
        mockMvc.perform(get("/api/admin/itens/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.codigo").value("OLEO-001"));
    }

    @Test
    void shouldReturnNotFoundWhenInventoryItemDoesNotExist() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(inventoryItemService.findById(id))
                .thenThrow(new InventoryItemNotFoundException("Item de estoque não encontrado"));

        // When / Then
        mockMvc.perform(get("/api/admin/itens/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Item de estoque não encontrado"));
    }

    @Test
    void shouldReturnAllInventoryItems() throws Exception {
        // Given
        when(inventoryItemService.findAll()).thenReturn(List.of(
                inventoryItem(UUID.randomUUID(), "OLEO-001", true, 10),
                inventoryItem(UUID.randomUUID(), "FILTRO-001", true, 5)
        ));

        // When / Then
        mockMvc.perform(get("/api/admin/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("OLEO-001"))
                .andExpect(jsonPath("$[1].codigo").value("FILTRO-001"));
    }

    @Test
    void shouldUpdateInventoryItem() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(inventoryItemService.update(any(UUID.class), any(InventoryItem.class)))
                .thenReturn(inventoryItem(id, "FILTRO-001", true, 7));

        // When / Then
        mockMvc.perform(put("/api/admin/itens/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validItemRequest().replace("OLEO-001", "FILTRO-001")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.codigo").value("FILTRO-001"));
    }

    @Test
    void shouldInactivateInventoryItem() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When / Then
        mockMvc.perform(patch("/api/admin/itens/{id}/inativar", id))
                .andExpect(status().isNoContent());

        verify(inventoryItemService).inactivate(id);
    }

    @Test
    void shouldActivateInventoryItem() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When / Then
        mockMvc.perform(patch("/api/admin/itens/{id}/ativar", id))
                .andExpect(status().isNoContent());

        verify(inventoryItemService).activate(id);
    }

    private static String validItemRequest() {
        return """
                {
                  "codigo": "OLEO-001",
                  "nome": "Oleo de motor",
                  "descricao": "Oleo sintetico 5W30",
                  "tipoItem": "PECA",
                  "unidadeMedida": "UNIDADE",
                  "custoUnitario": 45.90,
                  "precoVenda": 79.90,
                  "quantidadeEstoque": 10,
                  "estoqueMinimo": 3,
                  "marca": "MotorOil",
                  "veiculoAplicavel": "Universal",
                  "ativo": true
                }
                """;
    }

    @TestConfiguration
    static class MapperTestConfig {

        @Bean
        InventoryItemApiMapper inventoryItemApiMapper() {
            return Mappers.getMapper(InventoryItemApiMapper.class);
        }
    }
}
