package br.com.fiap.numberone.inventory.domain.entities;

import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementType;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryBusinessException;

import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryMovement {

    private UUID id;
    private UUID inventoryItemId;
    private InventoryMovementType movementType;
    private InventoryMovementOrigin movementOrigin;
    private UUID originReferenceId;
    private int quantityBefore;
    private int quantityAfter;
    private String observation;
    private UUID responsibleUserId;
    private LocalDateTime createdAt;

    private InventoryMovement() {
    }

    public static InventoryMovement createEntry(
            UUID inventoryItemId,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            int quantityBefore,
            int quantity,
            String observation,
            UUID responsibleUserId
    ) {
        if (quantity <= 0) {
            throw new InventoryBusinessException("Quantidade de entrada deve ser maior que zero");
        }

        int quantityAfter = quantityBefore + quantity;

        return buildNew(
                UUID.randomUUID(),
                inventoryItemId,
                InventoryMovementType.ENTRADA,
                movementOrigin,
                originReferenceId,
                quantityBefore,
                quantityAfter,
                observation,
                responsibleUserId,
                LocalDateTime.now()
        );
    }

    public static InventoryMovement createWithdrawal(
            UUID inventoryItemId,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            int quantityBefore,
            int quantity,
            String observation,
            UUID responsibleUserId
    ) {
        if (quantity <= 0) {
            throw new InventoryBusinessException("Quantidade de baixa deve ser maior que zero");
        }

        int quantityAfter = quantityBefore - quantity;

        if (quantityAfter < 0) {
            throw new InventoryBusinessException("Baixa não pode deixar o estoque negativo");
        }

        return buildNew(
                UUID.randomUUID(),
                inventoryItemId,
                InventoryMovementType.BAIXA,
                movementOrigin,
                originReferenceId,
                quantityBefore,
                quantityAfter,
                observation,
                responsibleUserId,
                LocalDateTime.now()
        );
    }

    public static InventoryMovement createAdjustment(
            UUID inventoryItemId,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            int quantityBefore,
            int quantityAfter,
            String observation,
            UUID responsibleUserId
    ) {
        if (quantityAfter < 0) {
            throw new InventoryBusinessException("Quantidade final do ajuste deve ser maior ou igual a zero");
        }

        if (observation == null || observation.isBlank()) {
            throw new InventoryBusinessException("Observação é obrigatória para ajuste");
        }

        return buildNew(
                UUID.randomUUID(),
                inventoryItemId,
                InventoryMovementType.AJUSTE,
                movementOrigin,
                originReferenceId,
                quantityBefore,
                quantityAfter,
                observation,
                responsibleUserId,
                LocalDateTime.now()
        );
    }

    public static InventoryMovement restore(
            UUID id,
            UUID inventoryItemId,
            InventoryMovementType movementType,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            int quantityBefore,
            int quantityAfter,
            String observation,
            UUID responsibleUserId,
            LocalDateTime createdAt
    ) {
        InventoryMovement movement = new InventoryMovement();
        movement.id = id;
        movement.inventoryItemId = inventoryItemId;
        movement.movementType = movementType;
        movement.movementOrigin = movementOrigin;
        movement.originReferenceId = originReferenceId;
        movement.quantityBefore = quantityBefore;
        movement.quantityAfter = quantityAfter;
        movement.observation = observation;
        movement.responsibleUserId = responsibleUserId;
        movement.createdAt = createdAt;
        return movement;
    }

    private static InventoryMovement buildNew(
            UUID id,
            UUID inventoryItemId,
            InventoryMovementType movementType,
            InventoryMovementOrigin movementOrigin,
            UUID originReferenceId,
            int quantityBefore,
            int quantityAfter,
            String observation,
            UUID responsibleUserId,
            LocalDateTime createdAt
    ) {
        if (id == null) {
            throw new InventoryBusinessException("Id da movimentação é obrigatório");
        }

        if (inventoryItemId == null) {
            throw new InventoryBusinessException("Id do item de estoque é obrigatório");
        }

        if (movementType == null) {
            throw new InventoryBusinessException("Tipo de movimentação é obrigatório");
        }

        if (movementOrigin == null) {
            throw new InventoryBusinessException("Origem da movimentação é obrigatória");
        }

        if (responsibleUserId == null) {
            throw new InventoryBusinessException("Usuário responsável é obrigatório");
        }

        if (createdAt == null) {
            throw new InventoryBusinessException("Data de criação da movimentação é obrigatória");
        }

        if (quantityBefore < 0) {
            throw new InventoryBusinessException("Quantidade antes não pode ser negativa");
        }

        if (quantityAfter < 0) {
            throw new InventoryBusinessException("Quantidade depois não pode ser negativa");
        }

        InventoryMovement movement = new InventoryMovement();
        movement.id = id;
        movement.inventoryItemId = inventoryItemId;
        movement.movementType = movementType;
        movement.movementOrigin = movementOrigin;
        movement.originReferenceId = originReferenceId;
        movement.quantityBefore = quantityBefore;
        movement.quantityAfter = quantityAfter;
        movement.observation = observation;
        movement.responsibleUserId = responsibleUserId;
        movement.createdAt = createdAt;
        return movement;
    }

    public UUID getId() {
        return id;
    }

    public UUID getInventoryItemId() {
        return inventoryItemId;
    }

    public InventoryMovementType getMovementType() {
        return movementType;
    }

    public InventoryMovementOrigin getMovementOrigin() {
        return movementOrigin;
    }

    public UUID getOriginReferenceId() {
        return originReferenceId;
    }

    public int getQuantityBefore() {
        return quantityBefore;
    }

    public int getQuantityAfter() {
        return quantityAfter;
    }

    public String getObservation() {
        return observation;
    }

    public UUID getResponsibleUserId() {
        return responsibleUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}