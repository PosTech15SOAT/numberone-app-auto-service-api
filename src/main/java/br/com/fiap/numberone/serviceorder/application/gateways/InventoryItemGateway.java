package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemGateway {

    Optional<InventoryItem> findById(UUID id);
}
