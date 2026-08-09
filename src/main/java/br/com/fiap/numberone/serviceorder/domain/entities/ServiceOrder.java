package br.com.fiap.numberone.serviceorder.domain.entities;

import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.CustomerNotActiveException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderStatusException;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemEndStatusException;
import br.com.fiap.numberone.serviceorder.domain.references.Customer;
import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {

    private UUID id;
    private String initialDescription;
    private String diagnosisDescription;
    private String finalDiagnosisDescription;
    private String notes;
    private Customer customer;
    private Vehicle vehicle;
    private List<ServiceOrderItem> serviceItems = new ArrayList<>();
    private List<ServiceOrderBudget> budgets = new ArrayList<>();
    private ServiceOrderStatus status;
    private LocalDateTime entryDateTime;
    private LocalDateTime expectedDateTime;
    private LocalDateTime deliveryDateTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void attachCustomer(Customer customer) {
        if(!customer.getActive()){
            throw new CustomerNotActiveException("Customer is not active to be attached");
        }
        this.customer = customer;
    }

    public void attachVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void applyFinalDiagnosis(String finalDiagnosisDescription, String notes) {
        this.finalDiagnosisDescription = finalDiagnosisDescription;
        this.notes = notes;
    }

    public void defineExpectedDateTime(LocalDateTime expectedDateTime) {
        this.expectedDateTime = expectedDateTime;
    }

    public void updateStatus(ServiceOrderStatus serviceOrderStatus) {
        if (status == null) {
            this.status = serviceOrderStatus;
            return;
        }
        if (status == serviceOrderStatus) {
            return;
        }
        if (!isTransitionAllowed(serviceOrderStatus)) {
            throw new InvalidServiceOrderStatusException(
                    "Transition from " + status + " to " + serviceOrderStatus + " is not allowed"
            );
        }
        this.status = serviceOrderStatus;
    }

    private boolean isTransitionAllowed(ServiceOrderStatus nextStatus) {
        return switch (status) {
            case RECEIVED -> List.of(ServiceOrderStatus.IN_DIAGNOSIS, ServiceOrderStatus.CANCELLED).contains(nextStatus);
            case IN_DIAGNOSIS -> List.of(ServiceOrderStatus.WAITING_APPROVAL, ServiceOrderStatus.CANCELLED).contains(nextStatus);
            case WAITING_APPROVAL -> List.of(
                    ServiceOrderStatus.APPROVED,
                    ServiceOrderStatus.REJECTED,
                    ServiceOrderStatus.CANCELLED
            ).contains(nextStatus);
            case APPROVED -> List.of(ServiceOrderStatus.IN_PROGRESS, ServiceOrderStatus.CANCELLED).contains(nextStatus);
            case IN_PROGRESS -> List.of(ServiceOrderStatus.COMPLETED, ServiceOrderStatus.CANCELLED).contains(nextStatus);
            case COMPLETED, CANCELLED -> Objects.equals(ServiceOrderStatus.DELIVERED, nextStatus);
            case REJECTED -> List.of(ServiceOrderStatus.IN_DIAGNOSIS, ServiceOrderStatus.WAITING_APPROVAL).contains(nextStatus);
            case DELIVERED -> false;
        };
    }

    public void validateServiceItemsAreFinished() {
        boolean serviceItemNotEnded = serviceItems
                .stream()
                .anyMatch(serviceOrderItem -> List.of(
                        OrderItemStatus.PENDING, OrderItemStatus.IN_PROGRESS).contains(serviceOrderItem.getStatus())
                );

        if(serviceItemNotEnded) {
            throw new ServiceOrderItemEndStatusException("Service order contains service items pending or in progress status");
        }
    }

    public BigDecimal getServiceItemsTotalValue() {
        return serviceItems
                .stream()
                .filter(Objects::nonNull)
                .filter(serviceOrderItem -> serviceOrderItem.getStatus() != OrderItemStatus.CANCELLED)
                .map(this::calculateServiceItemTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateServiceItemTotalValue(ServiceOrderItem serviceOrderItem) {
        BigDecimal serviceValue = Objects.requireNonNullElse(serviceOrderItem.getValue(), BigDecimal.ZERO);
        BigDecimal suppliesValue = calculateServiceItemSuppliesTotalValue(serviceOrderItem);

        return serviceValue.add(suppliesValue);
    }

    private BigDecimal calculateServiceItemSuppliesTotalValue(ServiceOrderItem serviceOrderItem) {
        if (serviceOrderItem.getSupplies() == null) {
            return BigDecimal.ZERO;
        }

        return serviceOrderItem.getSupplies()
                .stream()
                .filter(Objects::nonNull)
                .map(this::calculateSupplyTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateSupplyTotalValue(ServiceOrderItemSupply supply) {
        if (supply.getInventoryItem() == null || supply.getInventoryItem().getSalePrice() == null || supply.getQuantityUsed() == null) {
            return BigDecimal.ZERO;
        }

        return supply.getInventoryItem()
                .getSalePrice()
                .multiply(BigDecimal.valueOf(supply.getQuantityUsed()));
    }
}
