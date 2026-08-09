package br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities;

import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryItemEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico_servico_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderItemSupplyEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordem_servico_servico", nullable = false)
    private ServiceOrderItemEntity serviceOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item_estoque")
    private InventoryItemEntity inventoryItemEntity;

    @Column(name = "quantidade_usada")
    private Integer quantityUsed;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
