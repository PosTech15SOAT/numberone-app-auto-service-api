package br.com.fiap.numberone.serviceorder.application.services;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemCompletionUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemStartUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.AutomotiveServiceGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryWithdrawalGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemAlreadyInStatusException;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceOrderItemService {
    private final ServiceOrderGateway serviceOrderGateway;
    private final ServiceOrderItemGateway serviceOrderItemGateway;
    private final AutomotiveServiceGateway automotiveServiceGateway;
    private final InventoryWithdrawalGateway inventoryWithdrawalGateway;

    public ServiceOrderItemService(
            ServiceOrderGateway serviceOrderGateway,
            ServiceOrderItemGateway serviceOrderItemGateway,
            AutomotiveServiceGateway automotiveServiceGateway,
            InventoryWithdrawalGateway inventoryWithdrawalGateway
    ) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.serviceOrderItemGateway = serviceOrderItemGateway;
        this.automotiveServiceGateway = automotiveServiceGateway;
        this.inventoryWithdrawalGateway = inventoryWithdrawalGateway;
    }

    public ServiceOrderItem createServiceOrderItem(ServiceOrderItem serviceOrderItem) {
        UUID serviceOrderId = serviceOrderItem.getServiceOrder().getId();
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        UUID automotiveServiceId = serviceOrderItem.getAutomotiveService().getId();
        AutomotiveService automotiveService = automotiveServiceGateway.findById(automotiveServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Automotive service not found for id: " + automotiveServiceId));

        serviceOrderItem.attachServiceOrder(serviceOrder);
        serviceOrderItem.attachAutomotiveService(automotiveService);

        return serviceOrderItemGateway.save(serviceOrderItem);
    }

    public void deleteServiceOrderItem(UUID id) {
        ServiceOrderItem serviceOrderItem = getServiceOrderItem(id);

        UUID serviceOrderId = serviceOrderItem.getServiceOrder().getId();
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        if (!List.of(ServiceOrderStatus.RECEIVED, ServiceOrderStatus.IN_DIAGNOSIS).contains(serviceOrder.getStatus())) {
            throw new InvalidServiceOrderStatusException("Service order status does not allow deleting service item: " + serviceOrder.getStatus());
        }

        serviceOrderItemGateway.deleteById(id);
    }

    @Transactional
    public ServiceOrderItem startServiceOrderItem(UUID id) {
        ServiceOrderItem serviceOrderItem = getServiceOrderItem(id);
        validateServiceOrderAllowsItemStart(serviceOrderItem);
        validateItemIsNotAlreadyInStatus(serviceOrderItem, OrderItemStatus.IN_PROGRESS);

        if (hasUnavailableSupply(serviceOrderItem)) {
            return moveItemToWaitingForSupplies(serviceOrderItem);
        }

        return startItemAndConsumeSupplies(serviceOrderItem);
    }

    public ServiceOrderItem cancelServiceOrderItem(UUID id) {
        ServiceOrderItem serviceOrderItem = getServiceOrderItem(id);
        validateItemIsNotAlreadyInStatus(serviceOrderItem, OrderItemStatus.CANCELLED);
        return changeServiceOrderItemStatus(serviceOrderItem, OrderItemStatus.CANCELLED);
    }

    public ServiceOrderItem completeServiceOrderItem(UUID id) {
        ServiceOrderItem serviceOrderItem = getServiceOrderItem(id);
        validateItemIsNotAlreadyInStatus(serviceOrderItem, OrderItemStatus.COMPLETED);

        serviceOrderItem.updateStatus(OrderItemStatus.COMPLETED);
        serviceOrderItem.defineEndDateTime(LocalDateTime.now());
        return serviceOrderItemGateway.complete(
                ServiceOrderItemCompletionUpdate.builder()
                        .serviceOrderItemId(serviceOrderItem.getId())
                        .endDateTime(serviceOrderItem.getEndDateTime())
                        .status(serviceOrderItem.getStatus())
                        .build()
        );
    }

    public ServiceOrderItem changeServiceOrderItemStatus(ServiceOrderItem serviceOrderItem, OrderItemStatus targetStatus) {
        serviceOrderItem.updateStatus(targetStatus);
        return serviceOrderItemGateway.updateStatus(serviceOrderItem.getId(), serviceOrderItem.getStatus());
    }

    private ServiceOrderItem getServiceOrderItem(UUID id) {
        return serviceOrderItemGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order item not found for id: " + id));
    }

    private ServiceOrder getServiceOrder(UUID serviceOrderId) {
        return serviceOrderGateway.findById(serviceOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found for id: " + serviceOrderId));
    }

    private void validateServiceOrderAllowsItemStart(ServiceOrderItem serviceOrderItem) {
        UUID serviceOrderId = serviceOrderItem.getServiceOrder().getId();
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        if (serviceOrder.getStatus() != ServiceOrderStatus.IN_PROGRESS) {
            throw new InvalidServiceOrderStatusException(
                    "Service order status does not allow starting service item: " + serviceOrder.getStatus()
            );
        }
    }

    private void validateItemIsNotAlreadyInStatus(ServiceOrderItem serviceOrderItem, OrderItemStatus targetStatus) {
        if (serviceOrderItem.getStatus() == targetStatus) {
            throw new ServiceOrderItemAlreadyInStatusException(
                    "Servico da ordem ja se encontra no status " + translateStatus(targetStatus)
            );
        }
    }

    private boolean hasUnavailableSupply(ServiceOrderItem serviceOrderItem) {
        return serviceOrderItem.getSupplies()
                .stream()
                .anyMatch(serviceOrderItemSupply -> !inventoryWithdrawalGateway.isAvailableForServiceOrderItem(
                        serviceOrderItemSupply.getInventoryItem().getId(),
                        serviceOrderItemSupply.getQuantityUsed()
                ));
    }

    private ServiceOrderItem moveItemToWaitingForSupplies(ServiceOrderItem serviceOrderItem) {
        return changeServiceOrderItemStatus(serviceOrderItem, OrderItemStatus.WAITING_FOR_PARTS_AND_SUPPLIES);
    }

    private ServiceOrderItem startItemAndConsumeSupplies(ServiceOrderItem serviceOrderItem) {
        serviceOrderItem.updateStatus(OrderItemStatus.IN_PROGRESS);
        serviceOrderItem.defineStartDateTime(LocalDateTime.now());
        ServiceOrderItem startedServiceOrderItem = serviceOrderItemGateway.start(
                ServiceOrderItemStartUpdate.builder()
                        .serviceOrderItemId(serviceOrderItem.getId())
                        .startDateTime(serviceOrderItem.getStartDateTime())
                        .status(serviceOrderItem.getStatus())
                        .build()
        );
        consumeSupplies(startedServiceOrderItem);
        return startedServiceOrderItem;
    }

    private void consumeSupplies(ServiceOrderItem serviceOrderItem) {
        serviceOrderItem.getSupplies().forEach(serviceOrderItemSupply ->
                inventoryWithdrawalGateway.withdrawForServiceOrderItem(
                        serviceOrderItemSupply.getInventoryItem().getId(),
                        serviceOrderItemSupply.getQuantityUsed(),
                        serviceOrderItemSupply.getId()
                )
        );
    }

    private String translateStatus(OrderItemStatus status) {
        return switch (status) {
            case PENDING -> "PENDENTE";
            case WAITING_FOR_PARTS_AND_SUPPLIES -> "AGUARDANDO_PECAS_E_INSUMOS";
            case IN_PROGRESS -> "EM_EXECUCAO";
            case CANCELLED -> "CANCELADO";
            case COMPLETED -> "FINALIZADO";
        };
    }
}
