package br.com.fiap.numberone.serviceorder.unit.application;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemSupplyUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemSupplyGateway;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemSupplyService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderItemStatusException;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeAutomotiveService;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.inventoryItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItemSupply;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOrderItemSupplyServiceTest {

    @Mock
    private ServiceOrderItemSupplyGateway serviceOrderItemSupplyGateway;

    @Mock
    private ServiceOrderItemGateway serviceOrderItemGateway;

    @Mock
    private InventoryItemGateway inventoryItemGateway;

    private ServiceOrderItemSupplyService service;

    @BeforeEach
    void setUp() {
        service = new ServiceOrderItemSupplyService(
                serviceOrderItemSupplyGateway,
                serviceOrderItemGateway,
                inventoryItemGateway
        );
    }

    @Test
    void shouldCreateItemSupplyWithValidatedItemAndInventoryItem() {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        ServiceOrderItem serviceOrderItem = pendingServiceOrderItem(serviceOrderItemId);
        InventoryItem inventoryItem = inventoryItem(inventoryItemId, new BigDecimal("25.00"));
        ServiceOrderItemSupply newSupply = ServiceOrderItemSupply.builder()
                .serviceOrderItem(ServiceOrderItem.builder().id(serviceOrderItemId).build())
                .inventoryItem(InventoryItem.builder().id(inventoryItemId).build())
                .quantityUsed(2)
                .build();

        when(serviceOrderItemGateway.findById(serviceOrderItemId)).thenReturn(Optional.of(serviceOrderItem));
        when(inventoryItemGateway.findById(inventoryItemId)).thenReturn(Optional.of(inventoryItem));
        when(serviceOrderItemSupplyGateway.save(any(ServiceOrderItemSupply.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderItemSupply result = service.createItemSupply(newSupply);

        // Assert
        assertThat(result.getServiceOrderItem()).isSameAs(serviceOrderItem);
        assertThat(result.getInventoryItem()).isSameAs(inventoryItem);
        verify(serviceOrderItemSupplyGateway).save(newSupply);
    }

    @Test
    void shouldThrowWhenCreatingSupplyForUnknownServiceOrderItem() {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();
        ServiceOrderItemSupply newSupply = ServiceOrderItemSupply.builder()
                .serviceOrderItem(ServiceOrderItem.builder().id(serviceOrderItemId).build())
                .inventoryItem(InventoryItem.builder().id(UUID.randomUUID()).build())
                .quantityUsed(2)
                .build();

        when(serviceOrderItemGateway.findById(serviceOrderItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createItemSupply(newSupply))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order item not found for id: " + serviceOrderItemId);
        verify(inventoryItemGateway, never()).findById(any());
        verify(serviceOrderItemSupplyGateway, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreatingSupplyForUnknownInventoryItem() {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        ServiceOrderItemSupply newSupply = ServiceOrderItemSupply.builder()
                .serviceOrderItem(ServiceOrderItem.builder().id(serviceOrderItemId).build())
                .inventoryItem(InventoryItem.builder().id(inventoryItemId).build())
                .quantityUsed(2)
                .build();

        when(serviceOrderItemGateway.findById(serviceOrderItemId)).thenReturn(Optional.of(pendingServiceOrderItem(serviceOrderItemId)));
        when(inventoryItemGateway.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createItemSupply(newSupply))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inventory item not found for id: " + inventoryItemId);
        verify(serviceOrderItemSupplyGateway, never()).save(any());
    }

    @Test
    void shouldNotCreateSupplyWhenServiceOrderItemIsCompleted() {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        ServiceOrderItemSupply newSupply = ServiceOrderItemSupply.builder()
                .serviceOrderItem(ServiceOrderItem.builder().id(serviceOrderItemId).build())
                .inventoryItem(InventoryItem.builder().id(inventoryItemId).build())
                .quantityUsed(2)
                .build();

        when(serviceOrderItemGateway.findById(serviceOrderItemId))
                .thenReturn(Optional.of(serviceOrderItemWithStatus(serviceOrderItemId, OrderItemStatus.COMPLETED)));
        when(inventoryItemGateway.findById(inventoryItemId))
                .thenReturn(Optional.of(inventoryItem(inventoryItemId, new BigDecimal("25.00"))));

        // Act & Assert
        assertThatThrownBy(() -> service.createItemSupply(newSupply))
                .isInstanceOf(InvalidServiceOrderItemStatusException.class)
                .hasMessage("Service order item status does not allow deleting supply: COMPLETED");
        verify(serviceOrderItemSupplyGateway, never()).save(any());
    }

    @Test
    void shouldUpdateItemSupply() {
        // Arrange
        UUID supplyId = UUID.randomUUID();
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID newInventoryItemId = UUID.randomUUID();
        ServiceOrderItem serviceOrderItem = pendingServiceOrderItem(serviceOrderItemId);
        ServiceOrderItemSupply currentSupply = serviceOrderItemSupply(
                supplyId,
                ServiceOrderItem.builder().id(serviceOrderItemId).build(),
                inventoryItem(UUID.randomUUID(), new BigDecimal("10.00")),
                1
        );
        InventoryItem newInventoryItem = inventoryItem(newInventoryItemId, new BigDecimal("30.00"));
        ServiceOrderItemSupplyUpdate update = ServiceOrderItemSupplyUpdate.builder()
                .inventoryItemId(newInventoryItemId)
                .quantityUsed(3)
                .build();

        when(serviceOrderItemSupplyGateway.findById(supplyId)).thenReturn(Optional.of(currentSupply));
        when(inventoryItemGateway.findById(newInventoryItemId)).thenReturn(Optional.of(newInventoryItem));
        when(serviceOrderItemGateway.findById(serviceOrderItemId)).thenReturn(Optional.of(serviceOrderItem));
        when(serviceOrderItemSupplyGateway.save(any(ServiceOrderItemSupply.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderItemSupply result = service.updateItemSupply(supplyId, update);

        // Assert
        assertThat(result.getInventoryItem()).isSameAs(newInventoryItem);
        assertThat(result.getQuantityUsed()).isEqualTo(3);
        verify(serviceOrderItemSupplyGateway).save(currentSupply);
    }

    @Test
    void shouldNotUpdateSupplyWhenServiceOrderItemIsCancelled() {
        // Arrange
        UUID supplyId = UUID.randomUUID();
        UUID serviceOrderItemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        ServiceOrderItemSupply currentSupply = serviceOrderItemSupply(
                supplyId,
                ServiceOrderItem.builder().id(serviceOrderItemId).build(),
                inventoryItem(UUID.randomUUID(), new BigDecimal("10.00")),
                1
        );
        ServiceOrderItemSupplyUpdate update = ServiceOrderItemSupplyUpdate.builder()
                .inventoryItemId(inventoryItemId)
                .quantityUsed(3)
                .build();

        when(serviceOrderItemSupplyGateway.findById(supplyId)).thenReturn(Optional.of(currentSupply));
        when(inventoryItemGateway.findById(inventoryItemId))
                .thenReturn(Optional.of(inventoryItem(inventoryItemId, new BigDecimal("30.00"))));
        when(serviceOrderItemGateway.findById(serviceOrderItemId))
                .thenReturn(Optional.of(serviceOrderItemWithStatus(serviceOrderItemId, OrderItemStatus.CANCELLED)));

        // Act & Assert
        assertThatThrownBy(() -> service.updateItemSupply(supplyId, update))
                .isInstanceOf(InvalidServiceOrderItemStatusException.class)
                .hasMessage("Service order item status does not allow deleting supply: CANCELLED");
        verify(serviceOrderItemSupplyGateway, never()).save(any());
    }

    @Test
    void shouldListAllSuppliesWhenServiceOrderItemIdIsNull() {
        // Arrange
        ServiceOrderItemSupply firstSupply = serviceOrderItemSupply(
                UUID.randomUUID(),
                pendingServiceOrderItem(UUID.randomUUID()),
                inventoryItem(UUID.randomUUID(), new BigDecimal("10.00")),
                1
        );
        ServiceOrderItemSupply secondSupply = serviceOrderItemSupply(
                UUID.randomUUID(),
                pendingServiceOrderItem(UUID.randomUUID()),
                inventoryItem(UUID.randomUUID(), new BigDecimal("20.00")),
                2
        );
        when(serviceOrderItemSupplyGateway.findAll()).thenReturn(List.of(firstSupply, secondSupply));

        // Act
        List<ServiceOrderItemSupply> result = service.listItemSupply(null);

        // Assert
        assertThat(result).containsExactly(firstSupply, secondSupply);
        verify(serviceOrderItemSupplyGateway).findAll();
    }

    @Test
    void shouldListSuppliesByServiceOrderItemId() {
        // Arrange
        UUID serviceOrderItemId = UUID.randomUUID();
        ServiceOrderItemSupply supply = serviceOrderItemSupply(
                UUID.randomUUID(),
                pendingServiceOrderItem(serviceOrderItemId),
                inventoryItem(UUID.randomUUID(), new BigDecimal("10.00")),
                1
        );
        when(serviceOrderItemSupplyGateway.findByServiceOrderItemId(serviceOrderItemId)).thenReturn(List.of(supply));

        // Act
        List<ServiceOrderItemSupply> result = service.listItemSupply(serviceOrderItemId);

        // Assert
        assertThat(result).containsExactly(supply);
        verify(serviceOrderItemSupplyGateway).findByServiceOrderItemId(serviceOrderItemId);
    }

    @Test
    void shouldDeleteItemSupplyWhenServiceOrderItemAllowsChanges() {
        // Arrange
        UUID supplyId = UUID.randomUUID();
        UUID serviceOrderItemId = UUID.randomUUID();
        ServiceOrderItemSupply supply = serviceOrderItemSupply(
                supplyId,
                ServiceOrderItem.builder().id(serviceOrderItemId).build(),
                inventoryItem(UUID.randomUUID(), new BigDecimal("10.00")),
                1
        );

        when(serviceOrderItemSupplyGateway.findById(supplyId)).thenReturn(Optional.of(supply));
        when(serviceOrderItemGateway.findById(serviceOrderItemId)).thenReturn(Optional.of(pendingServiceOrderItem(serviceOrderItemId)));

        // Act
        service.deleteServiceOrderItemSupply(supplyId);

        // Assert
        verify(serviceOrderItemSupplyGateway).deleteById(supplyId);
    }

    @Test
    void shouldThrowWhenSupplyDoesNotExist() {
        // Arrange
        UUID supplyId = UUID.randomUUID();
        when(serviceOrderItemSupplyGateway.findById(supplyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.deleteServiceOrderItemSupply(supplyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order item supply not found for id: " + supplyId);
    }

    private ServiceOrderItem pendingServiceOrderItem(UUID serviceOrderItemId) {
        return serviceOrderItemWithStatus(serviceOrderItemId, OrderItemStatus.PENDING);
    }

    private ServiceOrderItem serviceOrderItemWithStatus(UUID serviceOrderItemId, OrderItemStatus status) {
        return serviceOrderItem(
                serviceOrderItemId,
                ServiceOrder.builder().id(UUID.randomUUID()).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                status
        );
    }
}
