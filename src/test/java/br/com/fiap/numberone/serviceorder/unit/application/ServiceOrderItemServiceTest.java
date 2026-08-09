package br.com.fiap.numberone.serviceorder.unit.application;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemCompletionUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemStartUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.AutomotiveServiceGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryWithdrawalGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemGateway;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.AutomotiveServiceNotActiveException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemAlreadyInStatusException;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeAutomotiveService;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.inactiveAutomotiveService;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.inventoryItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrder;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItemSupply;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOrderItemServiceTest {

    @Mock
    private ServiceOrderGateway serviceOrderGateway;

    @Mock
    private ServiceOrderItemGateway serviceOrderItemGateway;

    @Mock
    private AutomotiveServiceGateway automotiveServiceGateway;

    @Mock
    private InventoryWithdrawalGateway inventoryWithdrawalGateway;

    private ServiceOrderItemService service;

    @BeforeEach
    void setUp() {
        service = new ServiceOrderItemService(
                serviceOrderGateway,
                serviceOrderItemGateway,
                automotiveServiceGateway,
                inventoryWithdrawalGateway
        );
    }

    @Test
    void shouldCreateServiceOrderItemWithValidatedOrderAndAutomotiveService() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID automotiveServiceId = UUID.randomUUID();
        ServiceOrder order = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS);
        AutomotiveService automotiveService = activeAutomotiveService(automotiveServiceId);
        ServiceOrderItem newItem = ServiceOrderItem.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .automotiveService(AutomotiveService.builder().id(automotiveServiceId).build())
                .value(new BigDecimal("180.00"))
                .optional(false)
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(order));
        when(automotiveServiceGateway.findById(automotiveServiceId)).thenReturn(Optional.of(automotiveService));
        when(serviceOrderItemGateway.save(any(ServiceOrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderItem result = service.createServiceOrderItem(newItem);

        // Assert
        assertThat(result.getServiceOrder()).isSameAs(order);
        assertThat(result.getAutomotiveService()).isSameAs(automotiveService);
        verify(serviceOrderItemGateway).save(newItem);
    }

    @Test
    void shouldThrowWhenCreatingItemForUnknownServiceOrder() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrderItem newItem = ServiceOrderItem.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .automotiveService(AutomotiveService.builder().id(UUID.randomUUID()).build())
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createServiceOrderItem(newItem))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order not found for id: " + serviceOrderId);
        verify(automotiveServiceGateway, never()).findById(any());
        verify(serviceOrderItemGateway, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreatingItemForUnknownAutomotiveService() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID automotiveServiceId = UUID.randomUUID();
        ServiceOrderItem newItem = ServiceOrderItem.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .automotiveService(AutomotiveService.builder().id(automotiveServiceId).build())
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS)));
        when(automotiveServiceGateway.findById(automotiveServiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createServiceOrderItem(newItem))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Automotive service not found for id: " + automotiveServiceId);
        verify(serviceOrderItemGateway, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreatingItemWithInactiveAutomotiveService() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID automotiveServiceId = UUID.randomUUID();
        ServiceOrderItem newItem = ServiceOrderItem.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .automotiveService(AutomotiveService.builder().id(automotiveServiceId).build())
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS)));
        when(automotiveServiceGateway.findById(automotiveServiceId))
                .thenReturn(Optional.of(inactiveAutomotiveService(automotiveServiceId)));

        // Act & Assert
        assertThatThrownBy(() -> service.createServiceOrderItem(newItem))
                .isInstanceOf(AutomotiveServiceNotActiveException.class)
                .hasMessage("Automotive service is not active to be attached");
        verify(serviceOrderItemGateway, never()).save(any());
    }

    @Test
    void shouldDeleteServiceOrderItemWhenOrderAllowsChanges() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ServiceOrder order = serviceOrder(serviceOrderId, ServiceOrderStatus.RECEIVED);
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(order));

        // Act
        service.deleteServiceOrderItem(itemId);

        // Assert
        verify(serviceOrderItemGateway).deleteById(itemId);
    }

    @Test
    void shouldNotDeleteServiceOrderItemWhenOrderStatusDoesNotAllowChanges() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ServiceOrder order = serviceOrder(serviceOrderId, ServiceOrderStatus.APPROVED);
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> service.deleteServiceOrderItem(itemId))
                .isInstanceOf(InvalidServiceOrderStatusException.class)
                .hasMessage("Service order status does not allow deleting service item: APPROVED");
        verify(serviceOrderItemGateway, never()).deleteById(any());
    }

    @Test
    void shouldStartItemAndWithdrawAvailableSupplies() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        ServiceOrder orderReference = ServiceOrder.builder().id(serviceOrderId).build();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );
        InventoryItem inventoryItem = inventoryItem(inventoryItemId, new BigDecimal("25.00"));
        ServiceOrderItemSupply supply = serviceOrderItemSupply(UUID.randomUUID(), item, inventoryItem, 2);
        item.getSupplies().add(supply);

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderGateway.findById(serviceOrderId))
                .thenReturn(Optional.of(serviceOrder(serviceOrderId, ServiceOrderStatus.IN_PROGRESS)));
        when(inventoryWithdrawalGateway.isAvailableForServiceOrderItem(inventoryItemId, 2)).thenReturn(true);
        when(serviceOrderItemGateway.start(any(ServiceOrderItemStartUpdate.class))).thenReturn(item);

        LocalDateTime beforeStart = LocalDateTime.now();

        // Act
        ServiceOrderItem result = service.startServiceOrderItem(itemId);

        // Assert
        ArgumentCaptor<ServiceOrderItemStartUpdate> updateCaptor =
                ArgumentCaptor.forClass(ServiceOrderItemStartUpdate.class);
        verify(serviceOrderItemGateway).start(updateCaptor.capture());
        assertThat(result).isSameAs(item);
        assertThat(updateCaptor.getValue().getServiceOrderItemId()).isEqualTo(itemId);
        assertThat(updateCaptor.getValue().getStatus()).isEqualTo(OrderItemStatus.IN_PROGRESS);
        assertThat(updateCaptor.getValue().getStartDateTime()).isBetween(beforeStart, LocalDateTime.now());
        verify(inventoryWithdrawalGateway).withdrawForServiceOrderItem(inventoryItemId, 2, supply.getId());
    }

    @Test
    void shouldMoveItemToWaitingForSuppliesWhenAnySupplyIsUnavailable() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID inventoryItemId = UUID.randomUUID();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );
        item.getSupplies().add(serviceOrderItemSupply(
                UUID.randomUUID(),
                item,
                inventoryItem(inventoryItemId, new BigDecimal("25.00")),
                2
        ));

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderGateway.findById(serviceOrderId))
                .thenReturn(Optional.of(serviceOrder(serviceOrderId, ServiceOrderStatus.IN_PROGRESS)));
        when(inventoryWithdrawalGateway.isAvailableForServiceOrderItem(inventoryItemId, 2)).thenReturn(false);
        when(serviceOrderItemGateway.updateStatus(itemId, OrderItemStatus.WAITING_FOR_PARTS_AND_SUPPLIES))
                .thenReturn(item);

        // Act
        ServiceOrderItem result = service.startServiceOrderItem(itemId);

        // Assert
        assertThat(result).isSameAs(item);
        verify(serviceOrderItemGateway).updateStatus(itemId, OrderItemStatus.WAITING_FOR_PARTS_AND_SUPPLIES);
        verify(serviceOrderItemGateway, never()).start(any());
        verify(inventoryWithdrawalGateway, never()).withdrawForServiceOrderItem(any(), any(), any());
    }

    @Test
    void shouldNotStartItemWhenServiceOrderIsNotInProgress() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderGateway.findById(serviceOrderId))
                .thenReturn(Optional.of(serviceOrder(serviceOrderId, ServiceOrderStatus.APPROVED)));

        // Act & Assert
        assertThatThrownBy(() -> service.startServiceOrderItem(itemId))
                .isInstanceOf(InvalidServiceOrderStatusException.class)
                .hasMessage("Service order status does not allow starting service item: APPROVED");
        verify(serviceOrderItemGateway, never()).start(any());
    }

    @Test
    void shouldNotStartItemThatIsAlreadyInProgress() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.IN_PROGRESS
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderGateway.findById(serviceOrderId))
                .thenReturn(Optional.of(serviceOrder(serviceOrderId, ServiceOrderStatus.IN_PROGRESS)));

        // Act & Assert
        assertThatThrownBy(() -> service.startServiceOrderItem(itemId))
                .isInstanceOf(ServiceOrderItemAlreadyInStatusException.class)
                .hasMessage("Servico da ordem ja se encontra no status EM_EXECUCAO");
        verify(serviceOrderItemGateway, never()).start(any());
    }

    @Test
    void shouldCancelServiceOrderItem() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(UUID.randomUUID()).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderItemGateway.updateStatus(itemId, OrderItemStatus.CANCELLED)).thenReturn(item);

        // Act
        ServiceOrderItem result = service.cancelServiceOrderItem(itemId);

        // Assert
        assertThat(result).isSameAs(item);
        verify(serviceOrderItemGateway).updateStatus(itemId, OrderItemStatus.CANCELLED);
    }

    @Test
    void shouldNotCancelServiceOrderItemThatIsAlreadyCancelled() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(UUID.randomUUID()).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.CANCELLED
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));

        // Act & Assert
        assertThatThrownBy(() -> service.cancelServiceOrderItem(itemId))
                .isInstanceOf(ServiceOrderItemAlreadyInStatusException.class)
                .hasMessage("Servico da ordem ja se encontra no status CANCELADO");
        verify(serviceOrderItemGateway, never()).updateStatus(any(), any());
    }

    @Test
    void shouldCompleteServiceOrderItemAndRegisterEndDate() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(UUID.randomUUID()).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.IN_PROGRESS
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));
        when(serviceOrderItemGateway.complete(any(ServiceOrderItemCompletionUpdate.class))).thenReturn(item);
        LocalDateTime beforeComplete = LocalDateTime.now();

        // Act
        ServiceOrderItem result = service.completeServiceOrderItem(itemId);

        // Assert
        ArgumentCaptor<ServiceOrderItemCompletionUpdate> updateCaptor =
                ArgumentCaptor.forClass(ServiceOrderItemCompletionUpdate.class);
        verify(serviceOrderItemGateway).complete(updateCaptor.capture());
        assertThat(result).isSameAs(item);
        assertThat(updateCaptor.getValue().getServiceOrderItemId()).isEqualTo(itemId);
        assertThat(updateCaptor.getValue().getStatus()).isEqualTo(OrderItemStatus.COMPLETED);
        assertThat(updateCaptor.getValue().getEndDateTime()).isBetween(beforeComplete, LocalDateTime.now());
    }

    @Test
    void shouldNotCompleteServiceOrderItemThatIsAlreadyCompleted() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ServiceOrderItem item = serviceOrderItem(
                itemId,
                ServiceOrder.builder().id(UUID.randomUUID()).build(),
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.COMPLETED
        );

        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.of(item));

        // Act & Assert
        assertThatThrownBy(() -> service.completeServiceOrderItem(itemId))
                .isInstanceOf(ServiceOrderItemAlreadyInStatusException.class)
                .hasMessage("Servico da ordem ja se encontra no status FINALIZADO");
        verify(serviceOrderItemGateway, never()).complete(any());
    }

    @Test
    void shouldThrowWhenServiceOrderItemDoesNotExist() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(serviceOrderItemGateway.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.cancelServiceOrderItem(itemId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order item not found for id: " + itemId);
    }
}
