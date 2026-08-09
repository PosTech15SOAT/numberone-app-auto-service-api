package br.com.fiap.numberone.inventory.domain.entities;

import br.com.fiap.numberone.inventory.domain.enums.ItemType;
import br.com.fiap.numberone.inventory.domain.enums.UnitOfMeasure;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemAlreadyActiveException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemAlreadyInactiveException;
import br.com.fiap.numberone.inventory.domain.exceptions.InvalidInventoryItemDataException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemBusinessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryItem {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private ItemType itemType;
    private UnitOfMeasure unitOfMeasure;
    private BigDecimal costPerUnit;
    private BigDecimal salePrice;
    private Integer inventoryQuantity;
    private Integer minimumInventoryQuantity;
    private String brand;
    private String applicableVehicle;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private InventoryItem() {
    }

    public static InventoryItem create(
            String code,
            String name,
            String description,
            ItemType itemType,
            UnitOfMeasure unitOfMeasure,
            BigDecimal costPerUnit,
            BigDecimal salePrice,
            Integer inventoryQuantity,
            Integer minimumInventoryQuantity,
            String brand,
            String applicableVehicle,
            Boolean active
    ) {
        return buildNew(
                UUID.randomUUID(),
                code,
                name,
                description,
                itemType,
                unitOfMeasure,
                costPerUnit,
                salePrice,
                inventoryQuantity,
                minimumInventoryQuantity,
                brand,
                applicableVehicle,
                active != null ? active : true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static InventoryItem restore(
            UUID id,
            String code,
            String name,
            String description,
            ItemType itemType,
            UnitOfMeasure unitOfMeasure,
            BigDecimal costPerUnit,
            BigDecimal salePrice,
            Integer inventoryQuantity,
            Integer minimumInventoryQuantity,
            String brand,
            String applicableVehicle,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        InventoryItem item = new InventoryItem();
        item.id = id;
        item.code = code;
        item.name = name;
        item.description = description;
        item.itemType = itemType;
        item.unitOfMeasure = unitOfMeasure;
        item.costPerUnit = costPerUnit;
        item.salePrice = salePrice;
        item.inventoryQuantity = inventoryQuantity;
        item.minimumInventoryQuantity = minimumInventoryQuantity;
        item.brand = brand;
        item.applicableVehicle = applicableVehicle;
        item.active = active;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;
        return item;
    }

    private static InventoryItem buildNew(
            UUID id,
            String code,
            String name,
            String description,
            ItemType itemType,
            UnitOfMeasure unitOfMeasure,
            BigDecimal costPerUnit,
            BigDecimal salePrice,
            Integer inventoryQuantity,
            Integer minimumInventoryQuantity,
            String brand,
            String applicableVehicle,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (id == null) {
            throw new InvalidInventoryItemDataException("O id do item é obrigatório");
        }

        if (code == null || code.isBlank()) {
            throw new InvalidInventoryItemDataException("O código do item é obrigatório");
        }

        if (name == null || name.isBlank()) {
            throw new InvalidInventoryItemDataException("O nome do item é obrigatório");
        }

        if (itemType == null) {
            throw new InvalidInventoryItemDataException("O tipo do item é obrigatório");
        }

        if (unitOfMeasure == null) {
            throw new InvalidInventoryItemDataException("A unidade de medida do item é obrigatória");
        }

        if (costPerUnit == null) {
            throw new InvalidInventoryItemDataException("O custo unitário do item é obrigatório");
        }

        if (costPerUnit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInventoryItemDataException("O custo unitário do item deve ser maior que zero");
        }

        if (salePrice == null) {
            throw new InvalidInventoryItemDataException("O preço de venda do item é obrigatório");
        }

        if (salePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInventoryItemDataException("O preço de venda do item deve ser maior que zero");
        }

        if (inventoryQuantity == null) {
            throw new InvalidInventoryItemDataException("A quantidade em estoque é obrigatória");
        }

        if (inventoryQuantity < 0) {
            throw new InvalidInventoryItemDataException("A quantidade em estoque não pode ser negativa");
        }

        if (minimumInventoryQuantity == null) {
            throw new InvalidInventoryItemDataException("O estoque mínimo é obrigatório");
        }

        if (minimumInventoryQuantity < 0) {
            throw new InvalidInventoryItemDataException("O estoque mínimo não pode ser negativo");
        }

        if (active == null) {
            throw new InvalidInventoryItemDataException("O status ativo do item é obrigatório");
        }

        if (createdAt == null) {
            throw new InvalidInventoryItemDataException("A data de criação do item é obrigatória");
        }

        if (updatedAt == null) {
            throw new InvalidInventoryItemDataException("A data de atualização do item é obrigatória");
        }

        InventoryItem item = new InventoryItem();
        item.id = id;
        item.code = code;
        item.name = name;
        item.description = description;
        item.itemType = itemType;
        item.unitOfMeasure = unitOfMeasure;
        item.costPerUnit = costPerUnit;
        item.salePrice = salePrice;
        item.inventoryQuantity = inventoryQuantity;
        item.minimumInventoryQuantity = minimumInventoryQuantity;
        item.brand = brand;
        item.applicableVehicle = applicableVehicle;
        item.active = active;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;
        return item;
    }

    public void update(
            String code,
            String name,
            String description,
            ItemType itemType,
            UnitOfMeasure unitOfMeasure,
            BigDecimal costPerUnit,
            BigDecimal salePrice,
            Integer inventoryQuantity,
            Integer minimumInventoryQuantity,
            String brand,
            String applicableVehicle
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.itemType = itemType;
        this.unitOfMeasure = unitOfMeasure;
        this.costPerUnit = costPerUnit;
        this.salePrice = salePrice;
        this.inventoryQuantity = inventoryQuantity;
        this.minimumInventoryQuantity = minimumInventoryQuantity;
        this.brand = brand;
        this.applicableVehicle = applicableVehicle;
        this.updatedAt = LocalDateTime.now();

        validateForUpdate();
    }

    public void activate() {
        if (Boolean.TRUE.equals(this.active)) {
            throw new InventoryItemAlreadyActiveException("O item de estoque já está ativo");
        }

        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (Boolean.FALSE.equals(this.active)) {
            throw new InventoryItemAlreadyInactiveException("O item de estoque já está inativo");
        }

        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateForUpdate() {
        if (code == null || code.isBlank()) {
            throw new InvalidInventoryItemDataException("O código do item é obrigatório");
        }

        if (name == null || name.isBlank()) {
            throw new InvalidInventoryItemDataException("O nome do item é obrigatório");
        }

        if (itemType == null) {
            throw new InvalidInventoryItemDataException("O tipo do item é obrigatório");
        }

        if (unitOfMeasure == null) {
            throw new InvalidInventoryItemDataException("A unidade de medida do item é obrigatória");
        }

        if (costPerUnit == null) {
            throw new InvalidInventoryItemDataException("O custo unitário do item é obrigatório");
        }

        if (costPerUnit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInventoryItemDataException("O custo unitário do item deve ser maior que zero");
        }

        if (salePrice == null) {
            throw new InvalidInventoryItemDataException("O preço de venda do item é obrigatório");
        }

        if (salePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInventoryItemDataException("O preço de venda do item deve ser maior que zero");
        }

        if (inventoryQuantity == null) {
            throw new InvalidInventoryItemDataException("A quantidade em estoque é obrigatória");
        }

        if (inventoryQuantity < 0) {
            throw new InvalidInventoryItemDataException("A quantidade em estoque não pode ser negativa");
        }

        if (minimumInventoryQuantity == null) {
            throw new InvalidInventoryItemDataException("O estoque mínimo é obrigatório");
        }

        if (minimumInventoryQuantity < 0) {
            throw new InvalidInventoryItemDataException("O estoque mínimo não pode ser negativo");
        }
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public void addInventoryQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InventoryItemBusinessException("Quantidade de entrada deve ser maior que zero");
        }
        this.inventoryQuantity += quantity;
    }

    public void removeInventoryQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InventoryItemBusinessException("Quantidade de baixa deve ser maior que zero");
        }

        int newQuantity = this.inventoryQuantity - quantity;
        if (newQuantity < 0) {
            throw new InventoryItemBusinessException("Baixa não pode deixar o estoque negativo");
        }

        this.inventoryQuantity = newQuantity;
    }

    public void adjustInventoryQuantity(int finalQuantity) {
        if (finalQuantity < 0) {
            throw new InventoryItemBusinessException("Quantidade final do ajuste deve ser maior ou igual a zero");
        }

        this.inventoryQuantity = finalQuantity;
    }

    public boolean isBelowMinimumInventory() {
        return minimumInventoryQuantity != null
                && inventoryQuantity < minimumInventoryQuantity;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public Integer getInventoryQuantity() {
        return inventoryQuantity;
    }

    public Integer getMinimumInventoryQuantity() {
        return minimumInventoryQuantity;
    }

    public String getBrand() {
        return brand;
    }

    public String getApplicableVehicle() {
        return applicableVehicle;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}