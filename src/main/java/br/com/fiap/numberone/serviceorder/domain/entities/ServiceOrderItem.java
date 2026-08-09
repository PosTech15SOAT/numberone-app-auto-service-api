package br.com.fiap.numberone.serviceorder.domain.entities;

import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.AutomotiveServiceNotActiveException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderItemStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderStatusException;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderItem {

    private UUID id;
    private ServiceOrder serviceOrder;
    private AutomotiveService automotiveService;
    private BigDecimal value;
    private OrderItemStatus status;
    private Boolean optional;
    private List<ServiceOrderItemSupply> supplies = new ArrayList<>();
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void attachServiceOrder(ServiceOrder serviceOrder) {
        if (List.of(ServiceOrderStatus.CANCELLED, ServiceOrderStatus.COMPLETED, ServiceOrderStatus.DELIVERED).contains(serviceOrder.getStatus())) {
            throw new InvalidServiceOrderStatusException("Service order status does not allow attaching new services: " + serviceOrder.getStatus());
        }
        this.serviceOrder = serviceOrder;
    }

    public void attachAutomotiveService(AutomotiveService automotiveService) {
        if(!automotiveService.getActive()){
            throw new AutomotiveServiceNotActiveException("Automotive service is not active to be attached");
        }
        this.automotiveService = automotiveService;
    }

    public void updateStatus(OrderItemStatus orderItemStatus) {
        if (status == null) {
            this.status = orderItemStatus;
            return;
        }
        if (status == orderItemStatus) {
            return;
        }
        if (!isTransitionAllowed(orderItemStatus)) {
            throw new InvalidServiceOrderItemStatusException(
                    "Transition from " + status + " to " + orderItemStatus + " is not allowed"
            );
        }
        this.status = orderItemStatus;
    }

    public void defineStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void defineEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    private boolean isTransitionAllowed(OrderItemStatus nextStatus) {
        return switch (status) {
            case PENDING -> List.of(
                    OrderItemStatus.WAITING_FOR_PARTS_AND_SUPPLIES,
                    OrderItemStatus.IN_PROGRESS,
                    OrderItemStatus.CANCELLED
            ).contains(nextStatus);
            case WAITING_FOR_PARTS_AND_SUPPLIES -> List.of(
                    OrderItemStatus.IN_PROGRESS,
                    OrderItemStatus.CANCELLED
            ).contains(nextStatus);
            case IN_PROGRESS -> List.of(
                    OrderItemStatus.WAITING_FOR_PARTS_AND_SUPPLIES,
                    OrderItemStatus.COMPLETED,
                    OrderItemStatus.CANCELLED
            ).contains(nextStatus);
            case CANCELLED, COMPLETED -> false;
        };
    }

}
