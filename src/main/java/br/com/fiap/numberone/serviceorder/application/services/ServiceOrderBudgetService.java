package br.com.fiap.numberone.serviceorder.application.services;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetApprovalNotificationGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.CustomerEmailException;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import br.com.fiap.numberone.shared.application.gateways.LoggerGateway;
import br.com.fiap.numberone.shared.application.gateways.MetricsGateway;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.fiap.numberone.shared.config.utils.MetricNames.SERVICE_ORDER_STAGE_DURATION;

public class ServiceOrderBudgetService {

    private static final String STAGE_DIAGNOSIS = "diagnostico";

    private final ServiceOrderGateway serviceOrderGateway;
    private final ServiceOrderBudgetGateway serviceOrderBudgetGateway;
    private final ServiceOrderBudgetApprovalNotificationGateway serviceOrderBudgetApprovalNotificationGateway;
    private final MetricsGateway metricsGateway;
    private final LoggerGateway logger;

    public ServiceOrderBudgetService(
            ServiceOrderGateway serviceOrderGateway,
            ServiceOrderBudgetGateway serviceOrderBudgetGateway,
            ServiceOrderBudgetApprovalNotificationGateway serviceOrderBudgetApprovalNotificationGateway,
            MetricsGateway metricsGateway,
            LoggerGateway logger
    ) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.serviceOrderBudgetGateway = serviceOrderBudgetGateway;
        this.serviceOrderBudgetApprovalNotificationGateway =
                serviceOrderBudgetApprovalNotificationGateway;
        this.metricsGateway = metricsGateway;
        this.logger = logger;
    }

    public ServiceOrderBudget createDraftBudget(
            ServiceOrderBudget serviceOrderBudget
    ) {
        ServiceOrder serviceOrder =
                getServiceOrder(serviceOrderBudget);

        serviceOrderBudget.attachServiceOrder(serviceOrder);

        serviceOrderBudget.defineQuotedAmount(
                resolveQuotedAmount(
                        serviceOrderBudget.getQuotedAmount(),
                        serviceOrder
                )
        );

        ServiceOrderBudget createdBudget = serviceOrderBudgetGateway.save(
                serviceOrderBudget
        );

        logger.infoWithContext(
                "Orçamento criado",
                "service_order_id", createdBudget.getServiceOrder().getId(),
                "budget_id", createdBudget.getId(),
                "status", createdBudget.getStatus()
        );

        return createdBudget;
    }

    public ServiceOrderBudget requestApproval(UUID id) {
        ServiceOrderBudget serviceOrderBudget =
                getServiceOrderBudget(id);

        ServiceOrder serviceOrder =
                getServiceOrder(serviceOrderBudget);
        ServiceOrderStatus previousStatus = serviceOrder.getStatus();

        serviceOrderBudget.attachServiceOrder(
                serviceOrder
        );

        serviceOrderBudget.defineQuotedAmount(
                resolveQuotedAmount(
                        serviceOrderBudget.getQuotedAmount(),
                        serviceOrder
                )
        );

        serviceOrderBudget.markAsSent();

        String recipientEmail =
                serviceOrder.getCustomer() != null
                        ? serviceOrder.getCustomer().getEmail()
                        : null;

        if (recipientEmail == null ||
                recipientEmail.isBlank()) {

            throw new CustomerEmailException(
                    "Customer email is required to request budget approval"
            );
        }

        LocalDateTime transitionDateTime =
                LocalDateTime.now();

        Duration diagnosisDuration =
                calculateDuration(
                        serviceOrder.getUpdatedAt(),
                        transitionDateTime
                );

        serviceOrder.updateStatus(
                ServiceOrderStatus.WAITING_APPROVAL
        );

        serviceOrderGateway.updateStatus(
                serviceOrder.getId(),
                serviceOrder.getStatus()
        );

        recordDiagnosisDuration(
                diagnosisDuration
        );

        ServiceOrderBudget savedBudget =
                serviceOrderBudgetGateway.save(
                        serviceOrderBudget
                );

        serviceOrderBudgetApprovalNotificationGateway
                .sendApprovalRequest(
                        serviceOrderBudget,
                        recipientEmail
                );

        logger.infoWithContext(
                "Orçamento enviado para aprovação",
                "service_order_id", serviceOrder.getId(),
                "budget_id", savedBudget.getId(),
                "previous_status", previousStatus,
                "new_status", serviceOrder.getStatus()
        );

        return savedBudget;
    }

    public ServiceOrderBudget approve(UUID id) {
        ServiceOrderBudget serviceOrderBudget =
                getServiceOrderBudget(id);

        serviceOrderBudget.approve();

        ServiceOrderBudget savedBudget =
                serviceOrderBudgetGateway.save(
                        serviceOrderBudget
                );

        ServiceOrder serviceOrder = updateServiceOrderStatus(
                serviceOrderBudget,
                ServiceOrderStatus.APPROVED
        );

        logger.infoWithContext(
                "Orçamento aprovado",
                "service_order_id", serviceOrder.getId(),
                "budget_id", savedBudget.getId(),
                "previous_status", ServiceOrderStatus.WAITING_APPROVAL,
                "new_status", serviceOrder.getStatus()
        );

        return savedBudget;
    }

    public ServiceOrderBudget reject(UUID id) {
        ServiceOrderBudget serviceOrderBudget =
                getServiceOrderBudget(id);

        serviceOrderBudget.reject();

        ServiceOrderBudget savedBudget =
                serviceOrderBudgetGateway.save(
                        serviceOrderBudget
                );

        updateServiceOrderStatus(
                serviceOrderBudget,
                ServiceOrderStatus.REJECTED
        );

        return savedBudget;
    }

    public UUID getCustomerId(UUID budgetId) {
        ServiceOrderBudget serviceOrderBudget =
                getServiceOrderBudget(budgetId);

        ServiceOrder serviceOrder =
                getServiceOrder(serviceOrderBudget);

        return serviceOrder.getCustomer() == null
                ? null
                : serviceOrder.getCustomer().getId();
    }

    private ServiceOrder updateServiceOrderStatus(
            ServiceOrderBudget serviceOrderBudget,
            ServiceOrderStatus status
    ) {
        ServiceOrder serviceOrder =
                getServiceOrder(serviceOrderBudget);

        serviceOrder.updateStatus(status);

        serviceOrderGateway.updateStatus(
                serviceOrder.getId(),
                serviceOrder.getStatus()
        );

        return serviceOrder;
    }

    private ServiceOrderBudget getServiceOrderBudget(
            UUID id
    ) {
        return serviceOrderBudgetGateway.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service order budget not found for id: " + id
                        )
                );
    }

    private ServiceOrder getServiceOrder(
            ServiceOrderBudget serviceOrderBudget
    ) {
        return serviceOrderGateway
                .findById(
                        serviceOrderBudget
                                .getServiceOrder()
                                .getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service order not found for id: "
                                        + serviceOrderBudget
                                        .getServiceOrder()
                                        .getId()
                        )
                );
    }

    private BigDecimal resolveQuotedAmount(
            BigDecimal quotedAmount,
            ServiceOrder serviceOrder
    ) {
        if (quotedAmount != null) {
            return quotedAmount;
        }

        return serviceOrder
                .getServiceItemsTotalValue();
    }

    private Duration calculateDuration(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        if (startDateTime == null ||
                endDateTime == null) {

            return null;
        }

        return Duration.between(
                startDateTime,
                endDateTime
        );
    }

    private void recordDiagnosisDuration(
            Duration duration
    ) {
        if (duration == null ||
                duration.isNegative()) {

            return;
        }

        metricsGateway.recordTimer(
                SERVICE_ORDER_STAGE_DURATION,
                duration,
                "stage", STAGE_DIAGNOSIS
        );
    }
}
