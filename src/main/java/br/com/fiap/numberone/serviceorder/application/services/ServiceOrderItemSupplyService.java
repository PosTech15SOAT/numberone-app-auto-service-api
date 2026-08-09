package br.com.fiap.numberone.serviceorder.application.services;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemSupplyUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemSupplyGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderItemStatusException;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public class ServiceOrderItemSupplyService {

    private final ServiceOrderItemSupplyGateway serviceOrderItemSupplyGateway;
    private final ServiceOrderItemGateway serviceOrderItemGateway;
    private final InventoryItemGateway inventoryItemGateway;

    public ServiceOrderItemSupplyService(
            ServiceOrderItemSupplyGateway serviceOrderItemSupplyGateway,
            ServiceOrderItemGateway serviceOrderItemGateway,
            InventoryItemGateway inventoryItemGateway
    ) {
        this.serviceOrderItemSupplyGateway = serviceOrderItemSupplyGateway;
        this.serviceOrderItemGateway = serviceOrderItemGateway;
        this.inventoryItemGateway = inventoryItemGateway;
    }

    public ServiceOrderItemSupply createItemSupply(ServiceOrderItemSupply serviceOrderItemSupply) {
        ServiceOrderItem serviceOrderItem = getServiceOrderItem(serviceOrderItemSupply.getServiceOrderItem().getId());
        InventoryItem inventoryItem = getInventoryItem(serviceOrderItemSupply.getInventoryItem().getId());

        verifyOrderItemStatus(serviceOrderItemSupply);

        serviceOrderItemSupply.attachServiceOrderItem(serviceOrderItem);
        serviceOrderItemSupply.attachInventoryItem(inventoryItem);

        return serviceOrderItemSupplyGateway.save(serviceOrderItemSupply);
    }

    public ServiceOrderItemSupply updateItemSupply(UUID id, ServiceOrderItemSupplyUpdate update) {
        ServiceOrderItemSupply currentSupply = getItemSupply(id);
        InventoryItem inventoryItem = getInventoryItem(update.getInventoryItemId());

        verifyOrderItemStatus(currentSupply);

        currentSupply.updateSupply(inventoryItem, update.getQuantityUsed());

        return serviceOrderItemSupplyGateway.save(currentSupply);
    }

    public List<ServiceOrderItemSupply> listItemSupply(UUID serviceOrderItemId) {
        if (serviceOrderItemId == null) {
            return serviceOrderItemSupplyGateway.findAll();
        }
        return serviceOrderItemSupplyGateway.findByServiceOrderItemId(serviceOrderItemId);
    }

    public void deleteServiceOrderItemSupply(UUID id) {
        ServiceOrderItemSupply serviceOrderItemSupply = getItemSupply(id);
        verifyOrderItemStatus(serviceOrderItemSupply);

        serviceOrderItemSupplyGateway.deleteById(id);
    }

    private ServiceOrderItemSupply getItemSupply(UUID id) {
        return serviceOrderItemSupplyGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order item supply not found for id: " + id));
    }

    private ServiceOrderItem getServiceOrderItem(UUID id) {
        return serviceOrderItemGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order item not found for id: " + id));
    }

    private InventoryItem getInventoryItem(UUID id) {
        return inventoryItemGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found for id: " + id));
    }

    private void verifyOrderItemStatus(ServiceOrderItemSupply serviceOrderItemSupply) {
        ServiceOrderItem serviceOrderItem = getServiceOrderItem(serviceOrderItemSupply.getServiceOrderItem().getId());
        OrderItemStatus serviceOrderItemStatus = serviceOrderItem.getStatus();

        if (List.of(OrderItemStatus.CANCELLED, OrderItemStatus.COMPLETED).contains(serviceOrderItemStatus)) {
            throw new InvalidServiceOrderItemStatusException(
                    "Service order item status does not allow deleting supply: " + serviceOrderItemStatus
            );
        }
    }
}
