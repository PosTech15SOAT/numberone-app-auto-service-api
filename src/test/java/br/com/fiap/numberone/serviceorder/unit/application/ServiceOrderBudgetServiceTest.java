package br.com.fiap.numberone.serviceorder.unit.application;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetApprovalNotificationGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderBudgetService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderBudgetStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.CustomerEmailException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderBudgetStatusException;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeAutomotiveService;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.activeCustomerWithoutEmail;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.budget;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrder;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderItem;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderWithCustomer;
import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrderWithItems;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOrderBudgetServiceTest {

    @Mock
    private ServiceOrderGateway serviceOrderGateway;

    @Mock
    private ServiceOrderBudgetGateway serviceOrderBudgetGateway;

    @Mock
    private ServiceOrderBudgetApprovalNotificationGateway notificationGateway;

    private ServiceOrderBudgetService service;

    @BeforeEach
    void setUp() {
        service = new ServiceOrderBudgetService(serviceOrderGateway, serviceOrderBudgetGateway, notificationGateway);
    }

    @Test
    void shouldCreateDraftBudgetWithProvidedQuotedAmount() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder serviceOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS);
        ServiceOrderBudget draftBudget = ServiceOrderBudget.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .quotedAmount(new BigDecimal("350.00"))
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderBudgetGateway.save(any(ServiceOrderBudget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderBudget result = service.createDraftBudget(draftBudget);

        // Assert
        assertThat(result.getServiceOrder()).isSameAs(serviceOrder);
        assertThat(result.getQuotedAmount()).isEqualByComparingTo(new BigDecimal("350.00"));
        verify(serviceOrderBudgetGateway).save(draftBudget);
    }

    @Test
    void shouldCreateDraftBudgetUsingServiceOrderTotalWhenQuotedAmountIsMissing() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder orderReference = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS);
        ServiceOrderItem item = serviceOrderItem(
                UUID.randomUUID(),
                orderReference,
                activeAutomotiveService(UUID.randomUUID()),
                new BigDecimal("210.00"),
                OrderItemStatus.PENDING
        );
        ServiceOrder serviceOrder = serviceOrderWithItems(
                serviceOrderId,
                ServiceOrderStatus.IN_DIAGNOSIS,
                List.of(item)
        );
        ServiceOrderBudget draftBudget = ServiceOrderBudget.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderBudgetGateway.save(any(ServiceOrderBudget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderBudget result = service.createDraftBudget(draftBudget);

        // Assert
        assertThat(result.getQuotedAmount()).isEqualByComparingTo(new BigDecimal("210.00"));
    }

    @Test
    void shouldThrowWhenCreatingDraftBudgetForUnknownServiceOrder() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrderBudget draftBudget = ServiceOrderBudget.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .build();

        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.createDraftBudget(draftBudget))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order not found for id: " + serviceOrderId);
        verify(serviceOrderBudgetGateway, never()).save(any());
    }

    @Test
    void shouldRequestApprovalAndNotifyCustomer() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        ServiceOrder serviceOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_DIAGNOSIS);
        ServiceOrderBudget draftBudget = budget(
                budgetId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                new BigDecimal("500.00"),
                ServiceOrderBudgetStatus.DRAFT
        );

        when(serviceOrderBudgetGateway.findById(budgetId)).thenReturn(Optional.of(draftBudget));
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderBudgetGateway.save(any(ServiceOrderBudget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderBudget result = service.requestApproval(budgetId);

        // Assert
        assertThat(result.getStatus()).isEqualTo(ServiceOrderBudgetStatus.SENT);
        assertThat(result.getSentAt()).isNotNull();
        assertThat(result.getServiceOrder()).isSameAs(serviceOrder);
        verify(serviceOrderGateway).updateStatus(serviceOrderId, ServiceOrderStatus.WAITING_APPROVAL);
        verify(notificationGateway).sendApprovalRequest(result, serviceOrder.getCustomer().getEmail());
    }

    @Test
    void shouldNotRequestApprovalWhenCustomerEmailIsMissing() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID serviceOrderId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        ServiceOrder serviceOrder = serviceOrderWithCustomer(
                serviceOrderId,
                ServiceOrderStatus.IN_DIAGNOSIS,
                activeCustomerWithoutEmail(customerId)
        );
        ServiceOrderBudget draftBudget = budget(
                budgetId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                new BigDecimal("500.00"),
                ServiceOrderBudgetStatus.DRAFT
        );

        when(serviceOrderBudgetGateway.findById(budgetId)).thenReturn(Optional.of(draftBudget));
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));

        // Act & Assert
        assertThatThrownBy(() -> service.requestApproval(budgetId))
                .isInstanceOf(CustomerEmailException.class)
                .hasMessage("Customer email is required to request budget approval");
        verify(serviceOrderGateway, never()).updateStatus(any(), any());
        verify(serviceOrderBudgetGateway, never()).save(any());
        verify(notificationGateway, never()).sendApprovalRequest(any(), any());
    }

    @Test
    void shouldApproveSentBudgetAndApproveServiceOrder() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        ServiceOrder serviceOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.WAITING_APPROVAL);
        ServiceOrderBudget sentBudget = budget(
                budgetId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                new BigDecimal("500.00"),
                ServiceOrderBudgetStatus.SENT
        );

        when(serviceOrderBudgetGateway.findById(budgetId)).thenReturn(Optional.of(sentBudget));
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderBudgetGateway.save(any(ServiceOrderBudget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderBudget result = service.approve(budgetId);

        // Assert
        ArgumentCaptor<ServiceOrderBudget> budgetCaptor = ArgumentCaptor.forClass(ServiceOrderBudget.class);
        verify(serviceOrderBudgetGateway).save(budgetCaptor.capture());
        assertThat(result.getStatus()).isEqualTo(ServiceOrderBudgetStatus.APPROVED);
        assertThat(result.getApprovedAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(result.getApprovedAt()).isNotNull();
        assertThat(budgetCaptor.getValue()).isSameAs(sentBudget);
        verify(serviceOrderGateway).updateStatus(serviceOrderId, ServiceOrderStatus.APPROVED);
    }

    @Test
    void shouldRejectSentBudgetAndRejectServiceOrder() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        ServiceOrder serviceOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.WAITING_APPROVAL);
        ServiceOrderBudget sentBudget = budget(
                budgetId,
                ServiceOrder.builder().id(serviceOrderId).build(),
                new BigDecimal("500.00"),
                ServiceOrderBudgetStatus.SENT
        );

        when(serviceOrderBudgetGateway.findById(budgetId)).thenReturn(Optional.of(sentBudget));
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(serviceOrder));
        when(serviceOrderBudgetGateway.save(any(ServiceOrderBudget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ServiceOrderBudget result = service.reject(budgetId);

        // Assert
        assertThat(result.getStatus()).isEqualTo(ServiceOrderBudgetStatus.REJECTED);
        verify(serviceOrderGateway).updateStatus(serviceOrderId, ServiceOrderStatus.REJECTED);
    }

    @Test
    void shouldThrowWhenApprovingBudgetThatWasNotSent() {
        // Arrange
        UUID budgetId = UUID.randomUUID();
        ServiceOrderBudget draftBudget = budget(
                budgetId,
                ServiceOrder.builder().id(UUID.randomUUID()).build(),
                new BigDecimal("500.00"),
                ServiceOrderBudgetStatus.DRAFT
        );

        when(serviceOrderBudgetGateway.findById(budgetId)).thenReturn(Optional.of(draftBudget));

        // Act & Assert
        assertThatThrownBy(() -> service.approve(budgetId))
                .isInstanceOf(InvalidServiceOrderBudgetStatusException.class)
                .hasMessage("Budget status does not allow approving: DRAFT");
        verify(serviceOrderBudgetGateway, never()).save(any());
        verify(serviceOrderGateway, never()).updateStatus(any(), any());
    }

    @Test
    void shouldThrowWhenBudgetDoesNotExist() {
        // Arrange
        UUID budgetId = UUID.randomUUID();
        when(serviceOrderBudgetGateway.findById(budgetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.approve(budgetId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order budget not found for id: " + budgetId);
    }
}
