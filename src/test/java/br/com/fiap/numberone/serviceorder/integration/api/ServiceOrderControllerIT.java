package br.com.fiap.numberone.serviceorder.integration.api;

import br.com.fiap.numberone.serviceorder.api.controllers.ServiceOrderController;
import br.com.fiap.numberone.serviceorder.api.controllers.ServiceOrderBudgetController;
import br.com.fiap.numberone.serviceorder.api.controllers.ServiceOrderItemController;
import br.com.fiap.numberone.serviceorder.api.controllers.ServiceOrderItemSupplyController;
import br.com.fiap.numberone.serviceorder.api.controllers.ServiceOrderTrackingController;
import br.com.fiap.numberone.serviceorder.api.exceptions.ServiceOrderExceptionHandler;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderApiMapperImpl;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderBudgetApiMapperImpl;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderBudgetStatusApiMapperImpl;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderItemApiMapperImpl;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderItemStatusApiMapperImpl;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderItemSupplyApiMapperImpl;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderStatusApiMapperImpl;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderTrackingApiMapperImpl;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderBudgetService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemSupplyService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderTrackingService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderBudgetStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemAlreadyInStatusException;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderValue;
import br.com.fiap.numberone.shared.api.exception.GlobalExceptionHandler;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import br.com.fiap.numberone.shared.security.infrastructure.authorization.AuthenticatedCustomerAccess;
import br.com.fiap.numberone.shared.security.application.gateways.AuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeAutomotiveService;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeCustomer;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.inventoryItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrder;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItemSupply;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ServiceOrderController.class,
        ServiceOrderBudgetController.class,
        ServiceOrderItemController.class,
        ServiceOrderItemSupplyController.class,
        ServiceOrderTrackingController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({
        ServiceOrderApiMapperImpl.class,
        ServiceOrderBudgetApiMapperImpl.class,
        ServiceOrderBudgetStatusApiMapperImpl.class,
        ServiceOrderItemApiMapperImpl.class,
        ServiceOrderItemStatusApiMapperImpl.class,
        ServiceOrderItemSupplyApiMapperImpl.class,
        ServiceOrderStatusApiMapperImpl.class,
        ServiceOrderTrackingApiMapperImpl.class,
        ServiceOrderExceptionHandler.class,
        GlobalExceptionHandler.class
})
class ServiceOrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceOrderService serviceOrderService;

    @MockitoBean
    private ServiceOrderBudgetService budgetService;

    @MockitoBean
    private ServiceOrderItemService itemService;

    @MockitoBean
    private ServiceOrderItemSupplyService supplyService;

    @MockitoBean
    private ServiceOrderTrackingService trackingService;

    @MockitoBean
    private AuthenticatedCustomerAccess authenticatedCustomerAccess;

    @MockitoBean
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Test
    void shouldCreateServiceOrder() throws Exception {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        ServiceOrder createdOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.RECEIVED);
        String requestBody = """
                {
                  "descricaoInicial": "Barulho no motor",
                  "descricaoDiagnostico": "Diagnostico inicial",
                  "observacao": "Cliente aguardando retorno",
                  "idCliente": "%s",
                  "idVeiculo": "%s",
                  "dataHoraEntrada": "2026-04-28T10:15:00"
                }
                """.formatted(customerId, vehicleId);

        when(serviceOrderService.createServiceOrder(any(ServiceOrder.class))).thenReturn(createdOrder);

        // Act & Assert
        mockMvc.perform(post("/api/admin/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/admin/ordens-servico/" + serviceOrderId)))
                .andExpect(jsonPath("$.id").value(serviceOrderId.toString()))
                .andExpect(jsonPath("$.descricaoInicial").value("Barulho ao ligar"))
                .andExpect(jsonPath("$.status").value("RECEBIDA"));

        ArgumentCaptor<ServiceOrder> orderCaptor = ArgumentCaptor.forClass(ServiceOrder.class);
        verify(serviceOrderService).createServiceOrder(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getCustomer().getId()).isEqualTo(customerId);
        assertThat(orderCaptor.getValue().getVehicle().getId()).isEqualTo(vehicleId);
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingInvalidServiceOrder() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "descricaoInicial": "",
                  "descricaoDiagnostico": "",
                  "dataHoraEntrada": null
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/admin/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());

        verify(serviceOrderService, never()).createServiceOrder(any());
    }

    @Test
    void shouldReturnNotFoundWhenServiceOrderDoesNotExist() throws Exception {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        when(serviceOrderService.getServiceOrder(serviceOrderId))
                .thenThrow(new ResourceNotFoundException("Service order not found for id: " + serviceOrderId));

        // Act & Assert
        mockMvc.perform(get("/api/admin/ordens-servico/{id}", serviceOrderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Service order not found for id: " + serviceOrderId));
    }

    @Test
    void shouldCalculateServiceOrderValue() throws Exception {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        when(serviceOrderService.calculateServices(serviceOrderId))
                .thenReturn(ServiceOrderValue.builder()
                        .serviceOrderId(serviceOrderId)
                        .totalValue(new BigDecimal("180.00"))
                        .build());

        // Act & Assert
        mockMvc.perform(get("/api/admin/ordens-servico/{id}/calcular-servicos", serviceOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrdemServico").value(serviceOrderId.toString()))
                .andExpect(jsonPath("$.valorTotal").value(180.00));
    }

    @Test
    void shouldCreateDraftBudgetForServiceOrder() throws Exception {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        ServiceOrderBudget createdBudget = ServiceOrderBudget.builder()
                .id(budgetId)
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .quotedAmount(new BigDecimal("450.00"))
                .status(ServiceOrderBudgetStatus.DRAFT)
                .build();

        when(budgetService.createDraftBudget(any(ServiceOrderBudget.class))).thenReturn(createdBudget);

        // Act & Assert
        mockMvc.perform(post("/api/admin/ordens-servico/{serviceOrderId}/orcamentos", serviceOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "valorProposto": 450.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/admin/orcamentos-ordem-servico/" + budgetId)))
                .andExpect(jsonPath("$.id").value(budgetId.toString()))
                .andExpect(jsonPath("$.idOrdemServico").value(serviceOrderId.toString()))
                .andExpect(jsonPath("$.valorProposto").value(450.00))
                .andExpect(jsonPath("$.status").value("RASCUNHO"));

        ArgumentCaptor<ServiceOrderBudget> budgetCaptor = ArgumentCaptor.forClass(ServiceOrderBudget.class);
        verify(budgetService).createDraftBudget(budgetCaptor.capture());
        assertThat(budgetCaptor.getValue().getServiceOrder().getId()).isEqualTo(serviceOrderId);
        assertThat(budgetCaptor.getValue().getQuotedAmount()).isEqualByComparingTo(new BigDecimal("450.00"));
    }

    @Test
    void shouldApproveBudgetFromPublicEmailLink() throws Exception {
        // Arrange
        UUID budgetId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(budgetService.getCustomerId(budgetId)).thenReturn(customerId);
        when(budgetService.approve(budgetId)).thenReturn(ServiceOrderBudget.builder()
                .id(budgetId)
                .status(ServiceOrderBudgetStatus.APPROVED)
                .build());

        // Act & Assert
        mockMvc.perform(get("/api/public/orcamentos-ordem-servico/{id}/aprovacao/aprovar", budgetId))
                .andExpect(status().isOk())
                .andExpect(content().string("Orcamento aprovado com sucesso."));

        verify(authenticatedCustomerAccess).requireCurrentUserOwnershipOrAdmin(customerId);
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingInvalidBudget() throws Exception {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/admin/ordens-servico/{serviceOrderId}/orcamentos", serviceOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "valorProposto": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(budgetService, never()).createDraftBudget(any());
    }

    @Test
    void shouldAddServiceOrderItem() throws Exception {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ServiceOrderItem createdItem = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                activeAutomotiveService(serviceId),
                new BigDecimal("170.00"),
                OrderItemStatus.PENDING
        );

        when(itemService.createServiceOrderItem(any(ServiceOrderItem.class))).thenReturn(createdItem);

        // Act & Assert
        mockMvc.perform(post("/api/admin/itens-ordem-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idServico": "%s",
                                  "idOrdemServico": "%s",
                                  "valor": 170.00,
                                  "opcional": false
                                }
                                """.formatted(serviceId, serviceOrderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId.toString()))
                .andExpect(jsonPath("$.idOrdemServico").value(serviceOrderId.toString()))
                .andExpect(jsonPath("$.servicoAutomotivo.id").value(serviceId.toString()))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        ArgumentCaptor<ServiceOrderItem> itemCaptor = ArgumentCaptor.forClass(ServiceOrderItem.class);
        verify(itemService).createServiceOrderItem(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getServiceOrder().getId()).isEqualTo(serviceOrderId);
        assertThat(itemCaptor.getValue().getAutomotiveService().getId()).isEqualTo(serviceId);
    }

    @Test
    void shouldReturnBusinessErrorWhenStartingItemAlreadyInProgress() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(itemService.startServiceOrderItem(itemId))
                .thenThrow(new ServiceOrderItemAlreadyInStatusException(
                        "Servico da ordem ja se encontra no status EM_EXECUCAO"
                ));

        // Act & Assert
        mockMvc.perform(patch("/api/admin/itens-ordem-servico/{id}/iniciar", itemId))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Servico da ordem ja se encontra no status EM_EXECUCAO"));
    }

    @Test
    void shouldRemoveServiceOrderItem() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/admin/itens-ordem-servico/{id}", itemId))
                .andExpect(status().isNoContent());

        verify(itemService).deleteServiceOrderItem(itemId);
    }

    @Test
    void shouldCreateServiceOrderItemSupply() throws Exception {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        UUID supplyId = UUID.randomUUID();
        ServiceOrderItemSupply createdSupply = serviceOrderItemSupply(
                supplyId,
                ServiceOrderItem.builder().id(serviceOrderItemId).build(),
                inventoryItem(inventoryItemId, new BigDecimal("32.00")),
                3
        );

        when(supplyService.createItemSupply(any(ServiceOrderItemSupply.class))).thenReturn(createdSupply);

        // Act & Assert
        mockMvc.perform(post("/api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos", serviceOrderItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idItemEstoque": "%s",
                                  "quantidadeUsada": 3
                                }
                                """.formatted(inventoryItemId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/admin/itens-ordem-servico/" + serviceOrderItemId + "/insumos/" + supplyId)))
                .andExpect(jsonPath("$.id").value(supplyId.toString()))
                .andExpect(jsonPath("$.idOrdemServicoItem").value(serviceOrderItemId.toString()))
                .andExpect(jsonPath("$.itemEstoque.id").value(inventoryItemId.toString()))
                .andExpect(jsonPath("$.quantidadeUsada").value(3));

        ArgumentCaptor<ServiceOrderItemSupply> supplyCaptor = ArgumentCaptor.forClass(ServiceOrderItemSupply.class);
        verify(supplyService).createItemSupply(supplyCaptor.capture());
        assertThat(supplyCaptor.getValue().getServiceOrderItem().getId()).isEqualTo(serviceOrderItemId);
        assertThat(supplyCaptor.getValue().getInventoryItem().getId()).isEqualTo(inventoryItemId);
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingInvalidSupply() throws Exception {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos", serviceOrderItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantidadeUsada": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(supplyService, never()).createItemSupply(any());
    }

    @Test
    void shouldListServiceOrderItemSupplies() throws Exception {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        ServiceOrderItemSupply supply = serviceOrderItemSupply(
                UUID.randomUUID(),
                ServiceOrderItem.builder().id(serviceOrderItemId).build(),
                inventoryItem(inventoryItemId, new BigDecimal("32.00")),
                3
        );

        when(supplyService.listItemSupply(serviceOrderItemId)).thenReturn(List.of(supply));

        // Act & Assert
        mockMvc.perform(get("/api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos", serviceOrderItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrdemServicoItem").value(serviceOrderItemId.toString()))
                .andExpect(jsonPath("$[0].itemEstoque.id").value(inventoryItemId.toString()))
                .andExpect(jsonPath("$[0].quantidadeUsada").value(3));
    }

    @Test
    void shouldReturnPublicServiceOrderTracking() throws Exception {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        ServiceOrder serviceOrderReference = ServiceOrder.builder().id(serviceOrderId).build();
        ServiceOrderItem serviceOrderItem = serviceOrderItem(
                serviceOrderItemId,
                serviceOrderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("170.00"),
                OrderItemStatus.IN_PROGRESS
        );
        ServiceOrderBudget olderBudget = ServiceOrderBudget.builder()
                .id(UUID.randomUUID())
                .serviceOrder(serviceOrderReference)
                .quotedAmount(new BigDecimal("200.00"))
                .status(ServiceOrderBudgetStatus.SENT)
                .createdAt(LocalDateTime.of(2026, 4, 1, 10, 0))
                .build();
        ServiceOrderBudget latestBudget = ServiceOrderBudget.builder()
                .id(UUID.randomUUID())
                .serviceOrder(serviceOrderReference)
                .quotedAmount(new BigDecimal("250.00"))
                .approvedAmount(new BigDecimal("250.00"))
                .status(ServiceOrderBudgetStatus.APPROVED)
                .createdAt(LocalDateTime.of(2026, 4, 2, 10, 0))
                .approvedAt(LocalDateTime.of(2026, 4, 2, 11, 0))
                .build();
        ServiceOrder trackingOrder = ServiceOrder.builder()
                .id(serviceOrderId)
                .initialDescription("Barulho no motor")
                .finalDiagnosisDescription("Trocar correia")
                .vehicle(vehicle(vehicleId, UUID.randomUUID()))
                .customer(activeCustomer(customerId))
                .status(ServiceOrderStatus.APPROVED)
                .entryDateTime(LocalDateTime.of(2026, 4, 1, 9, 0))
                .expectedDateTime(LocalDateTime.of(2026, 4, 3, 18, 0))
                .budgets(List.of(olderBudget, latestBudget))
                .serviceItems(List.of(serviceOrderItem))
                .build();

        when(trackingService.getTracking(serviceOrderId)).thenReturn(trackingOrder);

        // Act & Assert
        mockMvc.perform(get("/api/public/ordens-servico/{id}/acompanhamento", serviceOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(serviceOrderId.toString()))
                .andExpect(jsonPath("$.status").value("APROVADA"))
                .andExpect(jsonPath("$.veiculo.placa").value("ABC1D23"))
                .andExpect(jsonPath("$.orcamento.valorProposto").value(250.00))
                .andExpect(jsonPath("$.orcamento.valorAprovado").value(250.00))
                .andExpect(jsonPath("$.orcamento.status").value("APROVADO"))
                .andExpect(jsonPath("$.itensServico[0].id").value(serviceOrderItemId.toString()))
                .andExpect(jsonPath("$.itensServico[0].nomeServico").value("Troca de oleo"))
                .andExpect(jsonPath("$.itensServico[0].status").value("EM_EXECUCAO"));

        verify(authenticatedCustomerAccess).requireCurrentUserOwnershipOrAdmin(customerId);
    }
}
