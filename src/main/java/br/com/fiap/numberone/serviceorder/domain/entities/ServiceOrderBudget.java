package br.com.fiap.numberone.serviceorder.domain.entities;

import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderBudgetStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderBudgetStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderStatusException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderBudget {

    private UUID id;
    private ServiceOrder serviceOrder;
    private BigDecimal quotedAmount;
    private BigDecimal approvedAmount;
    private ServiceOrderBudgetStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void attachServiceOrder(ServiceOrder serviceOrder) {
        if (!List.of(ServiceOrderStatus.IN_DIAGNOSIS, ServiceOrderStatus.REJECTED).contains(serviceOrder.getStatus())) {
            throw new InvalidServiceOrderStatusException("Service order status does not allow creating new budget: " + serviceOrder.getStatus());
        }
        this.serviceOrder = serviceOrder;
    }

    public void defineQuotedAmount(BigDecimal quotedAmount) {
        this.quotedAmount = quotedAmount;
    }

    public void markAsSent() {
        validateStatusTransition(ServiceOrderBudgetStatus.DRAFT, "sending");
        this.status = ServiceOrderBudgetStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void approve() {
        validateStatusTransition(ServiceOrderBudgetStatus.SENT, "approving");
        this.status = ServiceOrderBudgetStatus.APPROVED;
        this.approvedAmount = this.quotedAmount;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
        validateStatusTransition(ServiceOrderBudgetStatus.SENT, "rejecting");
        this.status = ServiceOrderBudgetStatus.REJECTED;
    }

    private void validateStatusTransition(ServiceOrderBudgetStatus expectedStatus, String action) {
        if (status != expectedStatus) {
            throw new InvalidServiceOrderBudgetStatusException(
                    "Budget status does not allow " + action + ": " + status
            );
        }
    }
}
