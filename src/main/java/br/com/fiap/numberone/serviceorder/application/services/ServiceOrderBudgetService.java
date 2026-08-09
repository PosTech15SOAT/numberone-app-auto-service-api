package br.com.fiap.numberone.serviceorder.application.services;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetApprovalNotificationGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.CustomerEmailException;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

public class ServiceOrderBudgetService {

    private final ServiceOrderGateway serviceOrderGateway;
    private final ServiceOrderBudgetGateway serviceOrderBudgetGateway;
    private final ServiceOrderBudgetApprovalNotificationGateway serviceOrderBudgetApprovalNotificationGateway;

    public ServiceOrderBudgetService(
            ServiceOrderGateway serviceOrderGateway,
            ServiceOrderBudgetGateway serviceOrderBudgetGateway,
            ServiceOrderBudgetApprovalNotificationGateway serviceOrderBudgetApprovalNotificationGateway
    ) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.serviceOrderBudgetGateway = serviceOrderBudgetGateway;
        this.serviceOrderBudgetApprovalNotificationGateway = serviceOrderBudgetApprovalNotificationGateway;
    }

    public ServiceOrderBudget createDraftBudget(ServiceOrderBudget serviceOrderBudget) {
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderBudget);

        serviceOrderBudget.attachServiceOrder(serviceOrder);
        serviceOrderBudget.defineQuotedAmount(resolveQuotedAmount(serviceOrderBudget.getQuotedAmount(), serviceOrder));

        return serviceOrderBudgetGateway.save(serviceOrderBudget);
    }

    public ServiceOrderBudget requestApproval(UUID id) {
        ServiceOrderBudget serviceOrderBudget = getServiceOrderBudget(id);

        ServiceOrder serviceOrder = getServiceOrder(serviceOrderBudget);

        serviceOrderBudget.attachServiceOrder(serviceOrder);
        serviceOrderBudget.defineQuotedAmount(resolveQuotedAmount(serviceOrderBudget.getQuotedAmount(), serviceOrder));
        serviceOrderBudget.markAsSent();

        String recipientEmail = serviceOrder.getCustomer() != null ? serviceOrder.getCustomer().getEmail() : null;
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new CustomerEmailException("Customer email is required to request budget approval");
        }
        serviceOrder.updateStatus(ServiceOrderStatus.WAITING_APPROVAL);
        serviceOrderGateway.updateStatus(serviceOrder.getId(), serviceOrder.getStatus());

        ServiceOrderBudget savedBudget = serviceOrderBudgetGateway.save(serviceOrderBudget);

        serviceOrderBudgetApprovalNotificationGateway.sendApprovalRequest(serviceOrderBudget, recipientEmail);

        return savedBudget;
    }

    public ServiceOrderBudget approve(UUID id) {
        ServiceOrderBudget serviceOrderBudget = getServiceOrderBudget(id);

        serviceOrderBudget.approve();
        ServiceOrderBudget savedBudget = serviceOrderBudgetGateway.save(serviceOrderBudget);

        updateServiceOrderStatus(serviceOrderBudget, ServiceOrderStatus.APPROVED);
        return savedBudget;
    }

    public ServiceOrderBudget reject(UUID id) {
        ServiceOrderBudget serviceOrderBudget = getServiceOrderBudget(id);

        serviceOrderBudget.reject();
        ServiceOrderBudget savedBudget = serviceOrderBudgetGateway.save(serviceOrderBudget);

        updateServiceOrderStatus(serviceOrderBudget, ServiceOrderStatus.REJECTED);
        return savedBudget;
    }

    private void updateServiceOrderStatus(ServiceOrderBudget serviceOrderBudget, ServiceOrderStatus approved) {
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderBudget);
        serviceOrder.updateStatus(approved);
        serviceOrderGateway.updateStatus(serviceOrder.getId(), serviceOrder.getStatus());
    }

    private ServiceOrderBudget getServiceOrderBudget(UUID id) {
        return serviceOrderBudgetGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order budget not found for id: " + id));
    }

    private ServiceOrder getServiceOrder(ServiceOrderBudget serviceOrderBudget) {
        return serviceOrderGateway.findById(serviceOrderBudget.getServiceOrder().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found for id: " + serviceOrderBudget.getServiceOrder().getId()));
    }

    private BigDecimal resolveQuotedAmount(BigDecimal quotedAmount, ServiceOrder serviceOrder) {
        if (quotedAmount != null) {
            return quotedAmount;
        }
        return serviceOrder.getServiceItemsTotalValue();
    }

}
