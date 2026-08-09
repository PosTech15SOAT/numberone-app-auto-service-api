package br.com.fiap.numberone.inventory.application.gateways;

import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementGateway {

    InventoryMovement save(InventoryMovement movement);

    List<InventoryMovement> findByInventoryItemId(UUID inventoryItemId);
}