package br.com.fiap.numberone.inventory.integration.api;

import br.com.fiap.numberone.inventory.api.controllers.InventoryMovementController;
import br.com.fiap.numberone.inventory.api.exceptions.InventoryMovementExceptionHandler;
import br.com.fiap.numberone.inventory.api.mappers.InventoryMovementApiMapper;
import br.com.fiap.numberone.inventory.application.services.InventoryMovementService;
import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryBusinessException;
import br.com.fiap.numberone.shared.api.exception.GlobalExceptionHandler;
import br.com.fiap.numberone.shared.security.infrastructure.repositories.AdminUserRepository;
import br.com.fiap.numberone.shared.security.infrastructure.token.JwtService;
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

import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.COMPRA;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ITEM_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ORIGIN_REFERENCE_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.RESPONSIBLE_USER_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.entryMovement;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.withdrawalMovement;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InventoryMovementController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        InventoryMovementControllerIT.MapperTestConfig.class,
        InventoryMovementExceptionHandler.class,
        GlobalExceptionHandler.class
})
class InventoryMovementControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryMovementService inventoryMovementService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AdminUserRepository adminUserRepository;

    @Test
    void shouldRegisterEntry() throws Exception {
        // Given
        when(inventoryMovementService.registerEntry(
                ITEM_ID, 5, COMPRA, ORIGIN_REFERENCE_ID, "Compra de reposicao", RESPONSIBLE_USER_ID
        )).thenReturn(entryMovement());

        // When / Then
        mockMvc.perform(post("/api/admin/estoque/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(entryMovement().getId().toString()))
                .andExpect(jsonPath("$.inventoryItemId").value(ITEM_ID.toString()))
                .andExpect(jsonPath("$.tipoMovimentacao").value("ENTRADA"))
                .andExpect(jsonPath("$.origemMovimentacao").value("COMPRA"))
                .andExpect(jsonPath("$.quantidadeAntes").value(10))
                .andExpect(jsonPath("$.quantidadeDepois").value(15));
    }

    @Test
    void shouldRegisterWithdrawal() throws Exception {
        // Given
        when(inventoryMovementService.registerWithdrawal(
                ITEM_ID, 4, br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.ORDEM_SERVICO,
                ORIGIN_REFERENCE_ID, "Uso em ordem de servico", RESPONSIBLE_USER_ID
        )).thenReturn(withdrawalMovement());

        // When / Then
        mockMvc.perform(post("/api/admin/estoque/baixa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idItemEstoque": "%s",
                                  "quantidade": 4,
                                  "origemMovimentacao": "ORDEM_SERVICO",
                                  "referenciaOrigemId": "%s",
                                  "observacao": "Uso em ordem de servico",
                                  "usuarioResponsavelId": "%s"
                                }
                                """.formatted(ITEM_ID, ORIGIN_REFERENCE_ID, RESPONSIBLE_USER_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimentacao").value("BAIXA"))
                .andExpect(jsonPath("$.quantidadeAntes").value(10))
                .andExpect(jsonPath("$.quantidadeDepois").value(6));
    }

    @Test
    void shouldRegisterAdjustment() throws Exception {
        // Given
        InventoryMovement adjustment = InventoryMovement.createAdjustment(
                ITEM_ID,
                br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.MANUAL,
                ORIGIN_REFERENCE_ID,
                10,
                7,
                "Contagem manual",
                RESPONSIBLE_USER_ID
        );
        when(inventoryMovementService.registerAdjustment(
                ITEM_ID,
                7,
                br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.MANUAL,
                ORIGIN_REFERENCE_ID,
                "Contagem manual",
                RESPONSIBLE_USER_ID
        )).thenReturn(adjustment);

        // When / Then
        mockMvc.perform(post("/api/admin/estoque/ajuste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idItemEstoque": "%s",
                                  "quantidadeFinal": 7,
                                  "origemMovimentacao": "MANUAL",
                                  "referenciaOrigemId": "%s",
                                  "observacao": "Contagem manual",
                                  "usuarioResponsavelId": "%s"
                                }
                                """.formatted(ITEM_ID, ORIGIN_REFERENCE_ID, RESPONSIBLE_USER_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimentacao").value("AJUSTE"))
                .andExpect(jsonPath("$.quantidadeAntes").value(10))
                .andExpect(jsonPath("$.quantidadeDepois").value(7));
    }

    @Test
    void shouldReturnValidationErrorsWhenEntryRequestIsInvalid() throws Exception {
        // Given / When / Then
        mockMvc.perform(post("/api/admin/estoque/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantidade": 0,
                                  "origemMovimentacao": null,
                                  "usuarioResponsavelId": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").isArray());

        verify(inventoryMovementService, never()).registerEntry(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldReturnBusinessErrorWhenMovementIsInvalid() throws Exception {
        // Given
        when(inventoryMovementService.registerEntry(
                ITEM_ID, 5, COMPRA, ORIGIN_REFERENCE_ID, "Compra de reposicao", RESPONSIBLE_USER_ID
        )).thenThrow(new InventoryBusinessException("Item de estoque inativo não pode receber movimentação"));

        // When / Then
        mockMvc.perform(post("/api/admin/estoque/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entryRequest()))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Item de estoque inativo não pode receber movimentação"));
    }

    @Test
    void shouldReturnMovementsByInventoryItemId() throws Exception {
        // Given
        when(inventoryMovementService.findByInventoryItemId(ITEM_ID))
                .thenReturn(List.of(entryMovement(), withdrawalMovement()));

        // When / Then
        mockMvc.perform(get("/api/admin/estoque/itens/{itemId}/movimentacoes", ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoMovimentacao").value("ENTRADA"))
                .andExpect(jsonPath("$[1].tipoMovimentacao").value("BAIXA"));
    }

    private static String entryRequest() {
        return """
                {
                  "idItemEstoque": "%s",
                  "quantidade": 5,
                  "origemMovimentacao": "COMPRA",
                  "referenciaOrigemId": "%s",
                  "observacao": "Compra de reposicao",
                  "usuarioResponsavelId": "%s"
                }
                """.formatted(ITEM_ID, ORIGIN_REFERENCE_ID, RESPONSIBLE_USER_ID);
    }

    @TestConfiguration
    static class MapperTestConfig {

        @Bean
        InventoryMovementApiMapper inventoryMovementApiMapper() {
            return Mappers.getMapper(InventoryMovementApiMapper.class);
        }
    }
}
