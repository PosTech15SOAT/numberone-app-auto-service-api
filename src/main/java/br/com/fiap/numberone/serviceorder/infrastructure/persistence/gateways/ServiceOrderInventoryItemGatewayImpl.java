package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryItemRepository;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.InventoryItemMapper;

import java.util.Optional;
import java.util.UUID;

public class ServiceOrderInventoryItemGatewayImpl implements InventoryItemGateway {

    private final InventoryItemRepository repository;
    private final InventoryItemMapper mapper;

    public ServiceOrderInventoryItemGatewayImpl(InventoryItemRepository repository, InventoryItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InventoryItem> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
