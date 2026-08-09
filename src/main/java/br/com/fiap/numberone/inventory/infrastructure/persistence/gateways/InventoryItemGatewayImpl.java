package br.com.fiap.numberone.inventory.infrastructure.persistence.gateways;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryItemEntity;
import br.com.fiap.numberone.inventory.infrastructure.persistence.mappers.InventoryItemEntityMapper;
import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InventoryItemGatewayImpl implements InventoryItemGateway {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemEntityMapper inventoryItemEntityMapper;

    public InventoryItemGatewayImpl(
            InventoryItemRepository inventoryItemRepository,
            InventoryItemEntityMapper inventoryItemEntityMapper
    ) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemEntityMapper = inventoryItemEntityMapper;
    }

    @Override
    public InventoryItem save(InventoryItem inventoryItem) {
        InventoryItemEntity inventoryItemEntity = inventoryItemEntityMapper.toEntity(inventoryItem);
        InventoryItemEntity savedEntity = inventoryItemRepository.save(inventoryItemEntity);
        return inventoryItemEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<InventoryItem> findById(UUID id) {
        return inventoryItemRepository.findById(id)
                .map(inventoryItemEntityMapper::toDomain);
    }

    @Override
    public Optional<InventoryItem> findByCode(String code) {
        return inventoryItemRepository.findByCode(code)
                .map(inventoryItemEntityMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return inventoryItemRepository.existsByCode(code);
    }

    @Override
    public List<InventoryItem> findAllActive() {
        return inventoryItemRepository.findByActiveTrue()
                .stream()
                .map(inventoryItemEntityMapper::toDomain)
                .toList();
    }
}