package br.com.fiap.numberone.inventory.infrastructure.config;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.application.gateways.InventoryMovementGateway;
import br.com.fiap.numberone.inventory.application.services.InventoryMovementService;
import br.com.fiap.numberone.inventory.infrastructure.persistence.gateways.InventoryMovementGatewayImpl;
import br.com.fiap.numberone.inventory.infrastructure.persistence.mappers.InventoryMovementEntityMapper;
import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryMovementRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryMovementBeansConfig {

    @Bean
    public InventoryMovementGateway inventoryMovementGateway(
            InventoryMovementRepository inventoryMovementRepository,
            InventoryMovementEntityMapper inventoryMovementEntityMapper
    ) {
        return new InventoryMovementGatewayImpl(inventoryMovementRepository, inventoryMovementEntityMapper);
    }

    @Bean
    public InventoryMovementService inventoryMovementService(
            InventoryItemGateway inventoryItemGateway,
            InventoryMovementGateway inventoryMovementGateway
            ) {
        return new InventoryMovementService(inventoryItemGateway,inventoryMovementGateway);
    }
}

