package br.com.fiap.numberone.inventory.infrastructure.persistence.entities;

import br.com.fiap.numberone.inventory.domain.enums.ItemType;
import br.com.fiap.numberone.inventory.domain.enums.UnitOfMeasure;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_estoque")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "nome", nullable = false, length = 150)
    private String name;

    @Column(name = "descricao", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 50)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false, length = 50)
    private UnitOfMeasure unitOfMeasure;

    @Column(name = "custo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerUnit;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer inventoryQuantity;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer minimumInventoryQuantity;

    @Column(name = "marca", length = 100)
    private String brand;

    @Column(name = "veiculo_aplicavel", length = 255)
    private String applicableVehicle;

    @Column(name = "ativo", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}