package br.com.fiap.numberone.inventory.support;

import br.com.fiap.numberone.inventory.api.dto.requests.InventoryItemRequest;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementType;
import br.com.fiap.numberone.inventory.domain.enums.ItemType;
import br.com.fiap.numberone.inventory.domain.enums.UnitOfMeasure;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryItemEntity;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryMovementEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class InventoryTestFactory {

    public static final UUID ITEM_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID MOVEMENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID RESPONSIBLE_USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID ORIGIN_REFERENCE_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    private InventoryTestFactory() {
    }

    public static InventoryItem inventoryItem() {
        return inventoryItem(ITEM_ID, "OLEO-001", true, 10);
    }

    public static InventoryItem inventoryItem(UUID id, String code, boolean active, int quantity) {
        return InventoryItem.restore(
                id,
                code,
                "Oleo de motor",
                "Oleo sintetico 5W30",
                ItemType.PECA,
                UnitOfMeasure.UNIDADE,
                new BigDecimal("45.90"),
                new BigDecimal("79.90"),
                quantity,
                3,
                "MotorOil",
                "Universal",
                active,
                LocalDateTime.of(2026, 4, 1, 10, 0),
                LocalDateTime.of(2026, 4, 1, 10, 0)
        );
    }

    public static InventoryItem newInventoryItem(String code) {
        return InventoryItem.create(
                code,
                "Filtro de oleo",
                "Filtro de oleo blindado",
                ItemType.PECA,
                UnitOfMeasure.UNIDADE,
                new BigDecimal("28.00"),
                new BigDecimal("52.00"),
                7,
                2,
                "FilterOne",
                "Universal",
                true
        );
    }

    public static InventoryItemRequest inventoryItemRequest() {
        return new InventoryItemRequest(
                "OLEO-001",
                "Oleo de motor",
                "Oleo sintetico 5W30",
                ItemType.PECA,
                UnitOfMeasure.UNIDADE,
                new BigDecimal("45.90"),
                new BigDecimal("79.90"),
                10,
                3,
                "MotorOil",
                "Universal",
                true
        );
    }

    public static InventoryMovement entryMovement() {
        return InventoryMovement.restore(
                MOVEMENT_ID,
                ITEM_ID,
                InventoryMovementType.ENTRADA,
                InventoryMovementOrigin.COMPRA,
                ORIGIN_REFERENCE_ID,
                10,
                15,
                "Compra de reposicao",
                RESPONSIBLE_USER_ID,
                LocalDateTime.of(2026, 4, 1, 11, 0)
        );
    }

    public static InventoryMovement withdrawalMovement() {
        return InventoryMovement.restore(
                MOVEMENT_ID,
                ITEM_ID,
                InventoryMovementType.BAIXA,
                InventoryMovementOrigin.ORDEM_SERVICO,
                ORIGIN_REFERENCE_ID,
                10,
                6,
                "Uso em ordem de servico",
                RESPONSIBLE_USER_ID,
                LocalDateTime.of(2026, 4, 1, 11, 0)
        );
    }

    public static InventoryItemEntity inventoryItemEntity(UUID id, String code, boolean active, int quantity) {
        return new InventoryItemEntity(
                id,
                code,
                "Oleo de motor",
                "Oleo sintetico 5W30",
                ItemType.PECA,
                UnitOfMeasure.UNIDADE,
                new BigDecimal("45.90"),
                new BigDecimal("79.90"),
                quantity,
                3,
                "MotorOil",
                "Universal",
                active,
                LocalDateTime.of(2026, 4, 1, 10, 0),
                LocalDateTime.of(2026, 4, 1, 10, 0)
        );
    }

    public static InventoryMovementEntity movementEntity(UUID id, UUID itemId) {
        return new InventoryMovementEntity(
                id,
                itemId,
                InventoryMovementType.ENTRADA,
                InventoryMovementOrigin.COMPRA,
                ORIGIN_REFERENCE_ID,
                10,
                15,
                "Compra de reposicao",
                RESPONSIBLE_USER_ID,
                LocalDateTime.of(2026, 4, 1, 11, 0)
        );
    }
}
