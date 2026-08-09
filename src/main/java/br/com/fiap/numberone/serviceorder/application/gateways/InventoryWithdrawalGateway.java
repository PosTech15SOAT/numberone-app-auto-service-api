package br.com.fiap.numberone.serviceorder.application.gateways;

import java.util.UUID;

public interface InventoryWithdrawalGateway {

    boolean isAvailableForServiceOrderItem(UUID inventoryItemId, Integer quantity);

    void withdrawForServiceOrderItem(UUID inventoryItemId, Integer quantity, UUID itemSupplyId);
}
