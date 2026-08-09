package br.com.fiap.numberone.inventory.infrastructure.persistence.gateways;

import br.com.fiap.numberone.inventory.application.gateways.InventoryMovementGateway;
import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryMovementEntity;
import br.com.fiap.numberone.inventory.infrastructure.persistence.mappers.InventoryMovementEntityMapper;
import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryMovementRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class InventoryMovementGatewayImpl implements InventoryMovementGateway {

    private final InventoryMovementRepository repository;
    private final InventoryMovementEntityMapper mapper;

    public InventoryMovementGatewayImpl(InventoryMovementRepository repository,
                                        InventoryMovementEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        InventoryMovementEntity entity = mapper.toEntity(movement);
        InventoryMovementEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<InventoryMovement> findByInventoryItemId(UUID inventoryItemId) {
        return repository.findByInventoryItemId(inventoryItemId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}