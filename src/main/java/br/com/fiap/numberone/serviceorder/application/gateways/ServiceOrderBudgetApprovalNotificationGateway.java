package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;

public interface ServiceOrderBudgetApprovalNotificationGateway {

    void sendApprovalRequest(ServiceOrderBudget serviceOrderBudget, String recipientEmail);
}
