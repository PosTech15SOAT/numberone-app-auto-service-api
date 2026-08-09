package br.com.fiap.numberone.inventory.api.dto.requests;

import br.com.fiap.numberone.inventory.domain.enums.ItemType;
import br.com.fiap.numberone.inventory.domain.enums.UnitOfMeasure;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemRequest {

    @JsonProperty("codigo")
    @Size(max = 100, message = "O campo codigo deve ter no máximo 100 caracteres.")
    private String code;

    @JsonProperty("nome")
    @NotBlank(message = "O campo nome é obrigatório.")
    @Size(max = 150, message = "O campo nome deve ter no máximo 150 caracteres.")
    private String name;

    @JsonProperty("descricao")
    @Size(max = 255, message = "O campo descricao deve ter no máximo 255 caracteres.")
    private String description;

    @JsonProperty("tipoItem")
    @NotNull(message = "O campo tipoItem é obrigatório.")
    private ItemType itemType;

    @JsonProperty("unidadeMedida")
    @NotNull(message = "O campo unidadeMedida é obrigatório.")
    private UnitOfMeasure unitOfMeasure;

    @JsonProperty("custoUnitario")
    @NotNull(message = "O campo custoUnitario é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O campo custoUnitario deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O campo custoUnitario deve ter no máximo 8 dígitos inteiros e 2 casas decimais.")
    private BigDecimal costPerUnit;

    @JsonProperty("precoVenda")
    @NotNull(message = "O campo precoVenda é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O campo precoVenda deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O campo precoVenda deve ter no máximo 8 dígitos inteiros e 2 casas decimais.")
    private BigDecimal salePrice;

    @JsonProperty("quantidadeEstoque")
    @NotNull(message = "O campo quantidadeEstoque é obrigatório.")
    @Min(value = 0, message = "O campo quantidadeEstoque não pode ser negativo.")
    private Integer inventoryQuantity;

    @JsonProperty("estoqueMinimo")
    @NotNull(message = "O campo estoqueMinimo é obrigatório.")
    @Min(value = 0, message = "O campo estoqueMinimo não pode ser negativo.")
    private Integer minimumInventoryQuantity;

    @JsonProperty("marca")
    @Size(max = 100, message = "O campo marca deve ter no máximo 100 caracteres.")
    private String brand;

    @JsonProperty("veiculoAplicavel")
    @Size(max = 255, message = "O campo veiculoAplicavel deve ter no máximo 255 caracteres.")
    private String applicableVehicle;

    @JsonProperty("ativo")
    private Boolean active;
}