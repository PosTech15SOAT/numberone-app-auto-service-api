package br.com.fiap.numberone.serviceorder.domain.entities;

import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderItemSupply {

    private UUID id;
    private ServiceOrderItem serviceOrderItem;
    private InventoryItem inventoryItem;
    private Integer quantityUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void attachServiceOrderItem(ServiceOrderItem serviceOrderItem) {
        this.serviceOrderItem = serviceOrderItem;
    }

    public void attachInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public void updateSupply(InventoryItem inventoryItem, Integer quantityUsed) {
        this.inventoryItem = inventoryItem;
        this.quantityUsed = quantityUsed;
    }
}
