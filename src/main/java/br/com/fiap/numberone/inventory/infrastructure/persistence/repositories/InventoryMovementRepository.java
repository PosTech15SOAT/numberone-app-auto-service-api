package br.com.fiap.numberone.inventory.infrastructure.persistence.repositories;

import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovementEntity, UUID> {

    List<InventoryMovementEntity> findByInventoryItemId(UUID inventoryItemId);
}