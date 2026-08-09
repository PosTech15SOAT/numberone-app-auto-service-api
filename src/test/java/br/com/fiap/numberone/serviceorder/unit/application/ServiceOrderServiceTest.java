package br.com.fiap.numberone.serviceorder.unit.application;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderDeliveryUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderFinalDiagnosisUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.CustomerGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.VehicleGateway;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemEndStatusException;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.serviceorder.domain.references.Customer;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.Diagnosis;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderAverageExecutionTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderEstimatedTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderValue;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeAutomotiveService;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeCustomer;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.inventoryItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrder;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItemSupply;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItemWithTimes;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderWithItems;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.vehicle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderGateway serviceOrderGateway;

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private VehicleGateway vehicleGateway;

    private ServiceOrderService service;

    @BeforeEach
    void setUp() {
        service = new ServiceOrderService(serviceOrderGateway, customerGateway, vehicleGateway);
    }

    @Test
    void shouldReturnAllServiceOrders() {
        // Arrange
        ServiceOrder firstOrder = serviceOrder(UUID.randomUUID(), ServiceOrderStatus.RECEIVED);
        ServiceOrder secondOrder = serviceOrder(UUID.randomUUID(), ServiceOrderStatus.IN_DIAGNOSIS);
        when(serviceOrderGateway.findAll()).thenReturn(List.of(firstOrder, secondOrder));

        // Act
        List<ServiceOrder> result = service.getServiceOrders();

        // Assert
        assertThat(result).containsExactly(firstOrder, secondOrder);
        verify(serviceOrderGateway).findAll();
    }

    @Test
    void shouldCreateServiceOrderWithValidatedCustomerAndVehicle() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        Customer validatedCustomer = activeCustomer(customerId);
        Vehicle validatedVehicle = vehicle(vehicleId, customerId);
        ServiceOrder newOrder = ServiceOrder.builder()
                .initialDescription("Barulho no motor")
                .diagnosisDescription("Diagnostico inicial")
                .customer(Customer.builder().id(customerId).build())
                .vehicle(Vehicle.builder().id(vehicleId).build())
                .build();

        when(customerGateway.findById(customerId)).thenReturn(Optional.of(validatedCustomer));
        when(vehicleGateway.findById(vehicleId)).thenReturn(Optional.of(validatedVehicle));
        when(serviceOrderGateway.save(any(ServiceOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrder result = service.createServiceOrder(newOrder);

        // Assert
        assertThat(result.getCustomer()).isSameAs(validatedCustomer);
        assertThat(result.getVehicle()).isSameAs(validatedVehicle);
        verify(serviceOrderGateway).save(newOrder);
    }

    @Test
    void shouldThrowWhenCreatingServiceOrderForUnknownCustomer() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        ServiceOrder newOrder = ServiceOrder.builder()
                .customer(Customer.builder().id(customerId).build())
                .vehicle(Vehicle.builder().id(UUID.randomUUID()).build())
                .build();
        when(customerGateway.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createServiceOrder(newOrder))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found");
        verify(vehicleGateway, never()).findById(any());
        verify(serviceOrderGateway, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreatingServiceOrderForUnknownVehicle() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        ServiceOrder newOrder = ServiceOrder.builder()
                .customer(Customer.builder().id(customerId).build())
                .vehicle(Vehicle.builder().id(vehicleId).build())
                .build();

        when(customerGateway.findById(customerId)).thenReturn(Optional.of(activeCustomer(customerId)));
        when(vehicleGateway.findById(vehicleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createServiceOrder(newOrder))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vehicle not found");
        verify(serviceOrderGateway, never()).save(any());
    }

    @Test
    void shouldAddFinalDiagnosisAndMoveOrderToDiagnosisStatus() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 4, 30, 15, 0);
        ServiceOrder existingOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.RECEIVED);
        Diagnosis diagnosis = Diagnosis.builder()
                .finalDiagnosisDescription("Bomba de combustivel com falha")
                .notes("Cliente autorizado")
                .expectedDateTime(expectedDateTime)
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));
        when(serviceOrderGateway.updateFinalDiagnosis(any(ServiceOrderFinalDiagnosisUpdate.class)))
                .thenReturn(existingOrder);

        // Act
        ServiceOrder result = service.addFinalDiagnosis(serviceOrderId, diagnosis);

        // Assert
        ArgumentCaptor<ServiceOrderFinalDiagnosisUpdate> updateCaptor =
                ArgumentCaptor.forClass(ServiceOrderFinalDiagnosisUpdate.class);
        verify(serviceOrderGateway).updateFinalDiagnosis(updateCaptor.capture());
        assertThat(result).isSameAs(existingOrder);
        assertThat(updateCaptor.getValue().getServiceOrderId()).isEqualTo(serviceOrderId);
        assertThat(updateCaptor.getValue().getFinalDiagnosisDescription())
                .isEqualTo("Bomba de combustivel com falha");
        assertThat(updateCaptor.getValue().getNotes()).isEqualTo("Cliente autorizado");
        assertThat(updateCaptor.getValue().getExpectedDateTime()).isEqualTo(expectedDateTime);
        assertThat(updateCaptor.getValue().getStatus()).isEqualTo(ServiceOrderStatus.IN_DIAGNOSIS);
    }

    @Test
    void shouldStartApprovedServiceOrder() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder existingOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.APPROVED);
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));
        when(serviceOrderGateway.updateStatus(serviceOrderId, ServiceOrderStatus.IN_PROGRESS)).thenReturn(existingOrder);

        // Act
        ServiceOrder result = service.startOrderService(serviceOrderId);

        // Assert
        assertThat(result).isSameAs(existingOrder);
        verify(serviceOrderGateway).updateStatus(serviceOrderId, ServiceOrderStatus.IN_PROGRESS);
    }

    @Test
    void shouldCancelReceivedServiceOrder() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder existingOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.RECEIVED);
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));
        when(serviceOrderGateway.updateStatus(serviceOrderId, ServiceOrderStatus.CANCELLED)).thenReturn(existingOrder);

        // Act
        ServiceOrder result = service.cancelOrderService(serviceOrderId);

        // Assert
        assertThat(result).isSameAs(existingOrder);
        verify(serviceOrderGateway).updateStatus(serviceOrderId, ServiceOrderStatus.CANCELLED);
    }

    @Test
    void shouldCompleteOrderWhenAllServiceItemsAreFinished() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder orderReference = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_PROGRESS);
        ServiceOrderItem completedItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.COMPLETED
        );
        ServiceOrder existingOrder = serviceOrderWithItems(
                serviceOrderId,
                ServiceOrderStatus.IN_PROGRESS,
                List.of(completedItem)
        );

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));
        when(serviceOrderGateway.updateStatus(serviceOrderId, ServiceOrderStatus.COMPLETED)).thenReturn(existingOrder);

        // Act
        ServiceOrder result = service.completeOrderService(serviceOrderId);

        // Assert
        assertThat(result).isSameAs(existingOrder);
        verify(serviceOrderGateway).updateStatus(serviceOrderId, ServiceOrderStatus.COMPLETED);
    }

    @Test
    void shouldNotCompleteOrderWhenAnyServiceItemIsPending() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder orderReference = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_PROGRESS);
        ServiceOrderItem pendingItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );
        ServiceOrder existingOrder = serviceOrderWithItems(
                serviceOrderId,
                ServiceOrderStatus.IN_PROGRESS,
                List.of(pendingItem)
        );

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));

        // Act & Assert
        assertThatThrownBy(() -> service.completeOrderService(serviceOrderId))
                .isInstanceOf(ServiceOrderItemEndStatusException.class)
                .hasMessage("Service order contains service items pending or in progress status");
        verify(serviceOrderGateway, never()).updateStatus(any(), any());
    }

    @Test
    void shouldDeliverCompletedOrderAndRegisterDeliveryDate() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder orderReference = serviceOrder(serviceOrderId, ServiceOrderStatus.COMPLETED);
        ServiceOrderItem completedItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.COMPLETED
        );
        ServiceOrder existingOrder = serviceOrderWithItems(
                serviceOrderId,
                ServiceOrderStatus.COMPLETED,
                List.of(completedItem)
        );
        LocalDateTime beforeDelivery = LocalDateTime.now();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));
        when(serviceOrderGateway.deliver(any(ServiceOrderDeliveryUpdate.class))).thenReturn(existingOrder);

        // Act
        ServiceOrder result = service.deliverOrderService(serviceOrderId);

        // Assert
        ArgumentCaptor<ServiceOrderDeliveryUpdate> updateCaptor =
                ArgumentCaptor.forClass(ServiceOrderDeliveryUpdate.class);
        verify(serviceOrderGateway).deliver(updateCaptor.capture());
        LocalDateTime afterDelivery = LocalDateTime.now();
        assertThat(result).isSameAs(existingOrder);
        assertThat(updateCaptor.getValue().getServiceOrderId()).isEqualTo(serviceOrderId);
        assertThat(updateCaptor.getValue().getStatus()).isEqualTo(ServiceOrderStatus.DELIVERED);
        assertThat(updateCaptor.getValue().getDeliveryDateTime()).isBetween(beforeDelivery, afterDelivery);
    }

    @Test
    void shouldCalculateServicesValueIgnoringCancelledItems() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder orderReference = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS);
        InventoryItem inventoryItem = inventoryItem(UUID.randomUUID(), new BigDecimal("20.00"));
        ServiceOrderItem activeItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );
        ServiceOrderItemSupply supply = serviceOrderItemSupply(UUID.randomUUID(), activeItem, inventoryItem, 2);
        activeItem.getSupplies().add(supply);
        ServiceOrderItem cancelledItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("999.00"),
                OrderItemStatus.CANCELLED
        );
        ServiceOrder existingOrder = serviceOrderWithItems(
                serviceOrderId,
                ServiceOrderStatus.IN_DIAGNOSIS,
                List.of(activeItem, cancelledItem)
        );

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));

        // Act
        ServiceOrderValue result = service.calculateServices(serviceOrderId);

        // Assert
        assertThat(result.getServiceOrderId()).isEqualTo(serviceOrderId);
        assertThat(result.getTotalValue()).isEqualByComparingTo(new BigDecimal("140.00"));
    }

    @Test
    void shouldCalculateEstimatedTimeIgnoringCancelledItemsAndNullServiceData() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder orderReference = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS);
        AutomotiveService quickService = AutomotiveService.builder()
                .id(UUID.randomUUID())
                .name("Alinhamento")
                .estimatedTimeMinutes(30)
                .active(true)
                .build();
        ServiceOrderItem firstItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.PENDING
        );
        ServiceOrderItem secondItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                quickService,
                new BigDecimal("80.00"),
                OrderItemStatus.IN_PROGRESS
        );
        ServiceOrderItem cancelledItem = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("100.00"),
                OrderItemStatus.CANCELLED
        );
        ServiceOrder existingOrder = serviceOrderWithItems(
                serviceOrderId,
                ServiceOrderStatus.IN_DIAGNOSIS,
                List.of(firstItem, secondItem, cancelledItem)
        );

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));
        LocalDateTime beforeCalculation = LocalDateTime.now();

        // Act
        ServiceOrderEstimatedTime result = service.calculateEstimatedTime(serviceOrderId);

        // Assert
        LocalDateTime afterCalculation = LocalDateTime.now();
        assertThat(result.getServiceOrderId()).isEqualTo(serviceOrderId);
        assertThat(result.getTotalEstimatedMinutes()).isEqualTo(90);
        assertThat(result.getSuggestedExpectedDateTime())
                .isBetween(beforeCalculation.plusMinutes(90), afterCalculation.plusMinutes(90));
    }

    @Test
    void shouldCalculateAverageServiceExecutionTimeAndStatusCounters() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder orderReference = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_PROGRESS);
        ServiceOrderItem completedOne = serviceOrderItemWithTimes(
                UUID.randomUUID(),
                orderReference,
                OrderItemStatus.COMPLETED,
                LocalDateTime.of(2026, 4, 1, 10, 0),
                LocalDateTime.of(2026, 4, 1, 11, 0)
        );
        ServiceOrderItem completedTwo = serviceOrderItemWithTimes(
                UUID.randomUUID(),
                orderReference,
                OrderItemStatus.COMPLETED,
                LocalDateTime.of(2026, 4, 1, 12, 0),
                LocalDateTime.of(2026, 4, 1, 14, 0)
        );
        ServiceOrderItem completedWithoutDates = serviceOrderItemWithTimes(
                UUID.randomUUID(),
                orderReference,
                OrderItemStatus.COMPLETED,
                null,
                null
        );
        ServiceOrder existingOrder = serviceOrderWithItems(
                serviceOrderId,
                ServiceOrderStatus.IN_PROGRESS,
                List.of(
                        completedOne,
                        completedTwo,
                        completedWithoutDates,
                        serviceOrderItemWithTimes(UUID.randomUUID(), orderReference, OrderItemStatus.PENDING, null, null),
                        serviceOrderItemWithTimes(UUID.randomUUID(), orderReference, OrderItemStatus.IN_PROGRESS, null, null),
                        serviceOrderItemWithTimes(UUID.randomUUID(), orderReference, OrderItemStatus.CANCELLED, null, null),
                        serviceOrderItemWithTimes(UUID.randomUUID(), orderReference, OrderItemStatus.WAITING_FOR_PARTS_AND_SUPPLIES, null, null)
                )
        );

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));

        // Act
        ServiceOrderAverageExecutionTime result = service.calculateAverageServiceExecutionTime(serviceOrderId);

        // Assert
        assertThat(result.getServiceOrderId()).isEqualTo(serviceOrderId);
        assertThat(result.getCompletedServices()).isEqualTo(3);
        assertThat(result.getPendingServices()).isEqualTo(1);
        assertThat(result.getInProgressServices()).isEqualTo(1);
        assertThat(result.getCancelledServices()).isEqualTo(1);
        assertThat(result.getWaitingServices()).isEqualTo(1);
        assertThat(result.getAverageExecutionMinutes()).isEqualTo(90);
    }

    @Test
    void shouldReturnServiceOrderById() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder existingOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.RECEIVED);
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));

        // Act
        ServiceOrder result = service.getServiceOrder(serviceOrderId);

        // Assert
        assertThat(result).isSameAs(existingOrder);
    }

    @Test
    void shouldThrowWhenServiceOrderDoesNotExist() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getServiceOrder(serviceOrderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order not found for id: " + serviceOrderId);
    }
}
