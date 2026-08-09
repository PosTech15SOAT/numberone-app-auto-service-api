package br.com.fiap.numberone.inventory.infrastructure.persistence.repositories;

import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, UUID> {

    Optional<InventoryItemEntity> findByCode(String code);

    boolean existsByCode(String code);

    List<InventoryItemEntity> findByActiveTrue();
}