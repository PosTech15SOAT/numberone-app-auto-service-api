package br.com.fiap.numberone.inventory.application.services;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.application.gateways.InventoryMovementGateway;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryBusinessException;

import java.util.List;
import java.util.UUID;

public class InventoryMovementService {

    private final InventoryItemGateway itemGateway;
    private final InventoryMovementGateway movementGateway;

    public InventoryMovementService(InventoryItemGateway itemGateway,
                                    InventoryMovementGateway movementGateway) {
        this.itemGateway = itemGateway;
        this.movementGateway = movementGateway;
    }

    public InventoryMovement registerEntry(
            UUID inventoryItemId,
            int quantity,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            String observation,
            UUID responsibleUserId
    ) {
        InventoryItem item = findAvailableItem(inventoryItemId);

        int quantityBefore = item.getInventoryQuantity();

        InventoryMovement movement = InventoryMovement.createEntry(
                inventoryItemId,
                movementOrigin,
                originReferenceId,
                quantityBefore,
                quantity,
                observation,
                responsibleUserId
        );

        item.addInventoryQuantity(quantity);

        itemGateway.save(item);
        return movementGateway.save(movement);
    }

    public InventoryMovement registerWithdrawal(
            UUID inventoryItemId,
            int quantity,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            String observation,
            UUID responsibleUserId
    ) {
        InventoryItem item = findAvailableItem(inventoryItemId);

        int quantityBefore = item.getInventoryQuantity();

        InventoryMovement movement = InventoryMovement.createWithdrawal(
                inventoryItemId,
                movementOrigin,
                originReferenceId,
                quantityBefore,
                quantity,
                observation,
                responsibleUserId
        );

        item.removeInventoryQuantity(quantity);

        itemGateway.save(item);
        InventoryMovement inventoryMovementResponse =  movementGateway.save(movement);

        if (item.isBelowMinimumInventory()) {
            // TODO: Implementar notificação de estoque baixo
        }

        return inventoryMovementResponse;
    }

    public InventoryMovement registerAdjustment(
            UUID inventoryItemId,
            int finalQuantity,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            String observation,
            UUID responsibleUserId
    ) {
        InventoryItem item = findAvailableItem(inventoryItemId);

        int quantityBefore = item.getInventoryQuantity();

        InventoryMovement movement = InventoryMovement.createAdjustment(
                inventoryItemId,
                movementOrigin,
                originReferenceId,
                quantityBefore,
                finalQuantity,
                observation,
                responsibleUserId
        );

        item.adjustInventoryQuantity(finalQuantity);

        itemGateway.save(item);
        InventoryMovement inventoryMovementResponse =  movementGateway.save(movement);

        if (item.isBelowMinimumInventory()) {
            // TODO: Implementar notificação de estoque baixo
        }

        return inventoryMovementResponse;
    }

    public List<InventoryMovement> findByInventoryItemId(UUID inventoryItemId) {
        return movementGateway.findByInventoryItemId(inventoryItemId);
    }

    private InventoryItem findAvailableItem(UUID inventoryItemId) {
        InventoryItem item = itemGateway.findById(inventoryItemId)
                .orElseThrow(() -> new InventoryBusinessException("Item de estoque não encontrado"));

        if (!item.isActive()) {
            throw new InventoryBusinessException("Item de estoque inativo não pode receber movimentação");
        }

        return item;
    }
}