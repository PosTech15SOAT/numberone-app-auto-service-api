package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.application.services.InventoryMovementService;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryBusinessException;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryWithdrawalGateway;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemSupplyUnavailableException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InventoryWithdrawalGatewayImpl implements InventoryWithdrawalGateway {

    private final InventoryItemGateway inventoryItemGateway;
    private final InventoryMovementService inventoryMovementService;

    public InventoryWithdrawalGatewayImpl(
            InventoryItemGateway inventoryItemGateway,
            InventoryMovementService inventoryMovementService
    ) {
        this.inventoryItemGateway = inventoryItemGateway;
        this.inventoryMovementService = inventoryMovementService;
    }

    @Override
    public boolean isAvailableForServiceOrderItem(UUID inventoryItemId, Integer quantity) {
        InventoryItem inventoryItem = inventoryItemGateway.findById(inventoryItemId)
                .orElseThrow(() -> new ServiceOrderItemSupplyUnavailableException(
                        "Inventory item unavailable: " + inventoryItemId
                ));

        return inventoryItem.isActive()
                && inventoryItem.getInventoryQuantity() != null
                && inventoryItem.getInventoryQuantity() >= quantity;
    }

    @Override
    public void withdrawForServiceOrderItem(UUID inventoryItemId, Integer quantity, UUID itemSupplyId) {
        UUID responsibleUserId = UUID.fromString("db0e6c07-2755-4093-b364-1f4ae360e587");

        try {
            inventoryMovementService.registerWithdrawal(
                    inventoryItemId,
                    quantity,
                    InventoryMovementOrigin.ORDEM_SERVICO,
                    itemSupplyId,
                    "Consumo de item na execucao da OS",
                    responsibleUserId
            );
        } catch (InventoryBusinessException ex) {
            throw new ServiceOrderItemSupplyUnavailableException(
                    "Inventory item unavailable: " + inventoryItemId
            );
        }
    }
}
