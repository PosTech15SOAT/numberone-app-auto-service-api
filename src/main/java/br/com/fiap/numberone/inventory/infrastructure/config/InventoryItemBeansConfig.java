package br.com.fiap.numberone.inventory.infrastructure.config;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.application.services.InventoryItemService;
import br.com.fiap.numberone.inventory.infrastructure.persistence.gateways.InventoryItemGatewayImpl;
import br.com.fiap.numberone.inventory.infrastructure.persistence.mappers.InventoryItemEntityMapper;
import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryItemRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryItemBeansConfig {

    @Bean
    public InventoryItemGateway inventoryItemGateway(InventoryItemRepository inventoryItemRepository, InventoryItemEntityMapper inventoryItemEntityMapper) {
        return new InventoryItemGatewayImpl(inventoryItemRepository, inventoryItemEntityMapper);
    }

    @Bean
    public InventoryItemService inventoryItemService(InventoryItemGateway inventoryItemGateway) {
        return new InventoryItemService(inventoryItemGateway);
    }
}

