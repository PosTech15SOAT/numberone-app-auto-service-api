package br.com.fiap.numberone.serviceorder.domain.references;

import br.com.fiap.numberone.inventory.domain.enums.ItemType;
import br.com.fiap.numberone.inventory.domain.enums.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
