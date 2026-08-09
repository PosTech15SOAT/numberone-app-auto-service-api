package br.com.fiap.numberone.inventory.application.services;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemBusinessException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemNotFoundException;

import java.util.List;
import java.util.UUID;

public class InventoryItemService {

    private final InventoryItemGateway inventoryItemGateway;

    public InventoryItemService(InventoryItemGateway inventoryItemGateway) {
        this.inventoryItemGateway = inventoryItemGateway;
    }

    public InventoryItem create(InventoryItem inventoryItem) {
        validateUniqueCodeForCreate(inventoryItem.getCode());
        return inventoryItemGateway.save(inventoryItem);
    }

    public InventoryItem update(UUID id, InventoryItem newData) {
        InventoryItem currentInventoryItem = inventoryItemGateway.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException("Item de estoque não encontrado"));

        validateUniqueCodeForUpdate(id, newData.getCode());

        currentInventoryItem.update(
                newData.getCode(),
                newData.getName(),
                newData.getDescription(),
                newData.getItemType(),
                newData.getUnitOfMeasure(),
                newData.getCostPerUnit(),
                newData.getSalePrice(),
                newData.getInventoryQuantity(),
                newData.getMinimumInventoryQuantity(),
                newData.getBrand(),
                newData.getApplicableVehicle()
        );

        return inventoryItemGateway.save(currentInventoryItem);
    }

    public List<InventoryItem> findAll() {
        return inventoryItemGateway.findAllActive();
    }

    public InventoryItem findById(UUID id) {
        return inventoryItemGateway.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException("Item de estoque não encontrado"));
    }

    private void validateUniqueCodeForCreate(String code) {
        if (inventoryItemGateway.existsByCode(code)) {
            throw new InventoryItemBusinessException("Já existe um item de estoque com o código informado");
        }
    }

    private void validateUniqueCodeForUpdate(UUID id, String code) {
        inventoryItemGateway.findByCode(code)
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new InventoryItemBusinessException("Já existe outro item de estoque com o código informado");
                });
    }

    public void inactivate(UUID id) {
        InventoryItem autoService = inventoryItemGateway.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException("Item de estoque não encontrado"));

        autoService.deactivate();

        inventoryItemGateway.save(autoService);
    }

    public void activate(UUID id) {
        InventoryItem autoService = inventoryItemGateway.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException("Item de estoque não encontrado"));

        autoService.activate();

        inventoryItemGateway.save(autoService);
    }
}