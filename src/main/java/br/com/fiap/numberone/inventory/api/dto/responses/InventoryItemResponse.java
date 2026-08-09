package br.com.fiap.numberone.inventory.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("codigo")
    private String code;

    @JsonProperty("nome")
    private String name;

    @JsonProperty("descricao")
    private String description;

    @JsonProperty("tipoItem")
    private String itemType;

    @JsonProperty("unidadeMedida")
    private String unitOfMeasure;

    @JsonProperty("custoUnitario")
    private BigDecimal costPerUnit;

    @JsonProperty("precoVenda")
    private BigDecimal salePrice;

    @JsonProperty("quantidadeEstoque")
    private Integer inventoryQuantity;

    @JsonProperty("estoqueMinimo")
    private Integer minimumInventoryQuantity;

    @JsonProperty("marca")
    private String brand;

    @JsonProperty("veiculoAplicavel")
    private String applicableVehicle;

    @JsonProperty("ativo")
    private Boolean active;

    @JsonProperty("criadoEm")
    private LocalDateTime createdAt;

    @JsonProperty("atualizadoEm")
    private LocalDateTime updatedAt;
}