package br.com.fiap.numberone.serviceorder.support;

import br.com.fiap.numberone.customer.domain.enums.TipoDocumento;
import br.com.fiap.numberone.inventory.domain.enums.ItemType;
import br.com.fiap.numberone.inventory.domain.enums.UnitOfMeasure;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderBudgetStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.serviceorder.domain.references.Customer;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ServiceOrderTestFixtures {

    private ServiceOrderTestFixtures() {
    }

    public static Customer activeCustomer(UUID id) {
        return Customer.builder()
                .id(id)
                .name("Ana Silva")
                .documentType(TipoDocumento.PESSOA_FISICA)
                .document("52998224725")
                .email("ana.silva@email.com")
                .phone("11999999999")
                .address("Rua A")
                .active(true)
                .build();
    }

    public static Customer activeCustomerWithoutEmail(UUID id) {
        return Customer.builder()
                .id(id)
                .name("Ana Silva")
                .documentType(TipoDocumento.PESSOA_FISICA)
                .document("52998224725")
                .phone("11999999999")
                .address("Rua A")
                .active(true)
                .build();
    }

    public static Customer inactiveCustomer(UUID id) {
        return Customer.builder()
                .id(id)
                .name("Cliente Inativo")
                .documentType(TipoDocumento.PESSOA_FISICA)
                .document("52998224725")
                .email("inativo@email.com")
                .phone("11999999999")
                .address("Rua A")
                .active(false)
                .build();
    }

    public static Vehicle vehicle(UUID id, UUID customerId) {
        return Vehicle.builder()
                .id(id)
                .licensePlate("ABC1D23")
                .brand("Fiat")
                .model("Argo")
                .year(2023)
                .customerId(customerId)
                .createdAt(LocalDateTime.of(2026, 4, 1, 10, 0))
                .build();
    }

    public static AutomotiveService activeAutomotiveService(UUID id) {
        return AutomotiveService.builder()
                .id(id)
                .code("SRV-001")
                .name("Troca de oleo")
                .description("Troca de oleo do motor")
                .serviceType("MAINTENANCE")
                .baseValue(new BigDecimal("120.00"))
                .estimatedTimeMinutes(60)
                .active(true)
                .build();
    }

    public static AutomotiveService inactiveAutomotiveService(UUID id) {
        return AutomotiveService.builder()
                .id(id)
                .code("SRV-002")
                .name("Servico inativo")
                .description("Servico indisponivel")
                .serviceType("MAINTENANCE")
                .baseValue(new BigDecimal("90.00"))
                .estimatedTimeMinutes(30)
                .active(false)
                .build();
    }

    public static InventoryItem inventoryItem(UUID id, BigDecimal salePrice) {
        return InventoryItem.builder()
                .id(id)
                .code("ITM-001")
                .name("Filtro de oleo")
                .description("Filtro de oleo do motor")
                .itemType(ItemType.PECA)
                .unitOfMeasure(UnitOfMeasure.UNIDADE)
                .salePrice(salePrice)
                .inventoryQuantity(10)
                .active(true)
                .build();
    }

    public static ServiceOrder serviceOrder(UUID id, ServiceOrderStatus status) {
        UUID customerId = UUID.randomUUID();
        return ServiceOrder.builder()
                .id(id)
                .initialDescription("Barulho ao ligar")
                .diagnosisDescription("Diagnostico inicial")
                .notes("Sem observacoes")
                .customer(activeCustomer(customerId))
                .vehicle(vehicle(UUID.randomUUID(), customerId))
                .serviceItems(new ArrayList<>())
                .budgets(new ArrayList<>())
                .status(status)
                .entryDateTime(LocalDateTime.of(2026, 4, 1, 9, 30))
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .build();
    }

    public static ServiceOrder serviceOrderWithCustomer(UUID id, ServiceOrderStatus status, Customer customer) {
        return ServiceOrder.builder()
                .id(id)
                .initialDescription("Barulho ao ligar")
                .diagnosisDescription("Diagnostico inicial")
                .notes("Sem observacoes")
                .customer(customer)
                .vehicle(vehicle(UUID.randomUUID(), customer.getId()))
                .serviceItems(new ArrayList<>())
                .budgets(new ArrayList<>())
                .status(status)
                .entryDateTime(LocalDateTime.of(2026, 4, 1, 9, 30))
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .build();
    }

    public static ServiceOrder serviceOrderWithItems(UUID id, ServiceOrderStatus status, List<ServiceOrderItem> items) {
        UUID customerId = UUID.randomUUID();
        return ServiceOrder.builder()
                .id(id)
                .initialDescription("Barulho ao ligar")
                .diagnosisDescription("Diagnostico inicial")
                .notes("Sem observacoes")
                .customer(activeCustomer(customerId))
                .vehicle(vehicle(UUID.randomUUID(), customerId))
                .serviceItems(new ArrayList<>(items))
                .budgets(new ArrayList<>())
                .status(status)
                .entryDateTime(LocalDateTime.of(2026, 4, 1, 9, 30))
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .build();
    }

    public static ServiceOrderItem serviceOrderItem(
            UUID id,
            ServiceOrder serviceOrder,
            AutomotiveService automotiveService,
            BigDecimal value,
            OrderItemStatus status
    ) {
        return ServiceOrderItem.builder()
                .id(id)
                .serviceOrder(serviceOrder)
                .automotiveService(automotiveService)
                .value(value)
                .status(status)
                .optional(false)
                .supplies(new ArrayList<>())
                .createdAt(LocalDateTime.of(2026, 4, 1, 10, 0))
                .build();
    }

    public static ServiceOrderItem serviceOrderItemWithTimes(
            UUID id,
            ServiceOrder serviceOrder,
            OrderItemStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        return ServiceOrderItem.builder()
                .id(id)
                .serviceOrder(serviceOrder)
                .automotiveService(activeAutomotiveService(UUID.randomUUID()))
                .value(new BigDecimal("100.00"))
                .status(status)
                .optional(false)
                .supplies(new ArrayList<>())
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

    public static ServiceOrderItemSupply serviceOrderItemSupply(
            UUID id,
            ServiceOrderItem serviceOrderItem,
            InventoryItem inventoryItem,
            Integer quantityUsed
    ) {
        return ServiceOrderItemSupply.builder()
                .id(id)
                .serviceOrderItem(serviceOrderItem)
                .inventoryItem(inventoryItem)
                .quantityUsed(quantityUsed)
                .build();
    }

    public static ServiceOrderBudget budget(
            UUID id,
            ServiceOrder serviceOrder,
            BigDecimal quotedAmount,
            ServiceOrderBudgetStatus status
    ) {
        return ServiceOrderBudget.builder()
                .id(id)
                .serviceOrder(serviceOrder)
                .quotedAmount(quotedAmount)
                .status(status)
                .createdAt(LocalDateTime.of(2026, 4, 1, 11, 0))
                .build();
    }
}


