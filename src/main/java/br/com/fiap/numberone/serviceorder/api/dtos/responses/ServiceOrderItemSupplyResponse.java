package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ServiceOrderItemSupplyResponse(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("idOrdemServicoItem")
        UUID serviceOrderItemId,
        @JsonProperty("itemEstoque")
        InventoryItemResponse inventoryItem,
        @JsonProperty("quantidadeUsada")
        Integer quantityUsed
) {
    public record InventoryItemResponse (
            @JsonProperty("id")
            UUID id,
            @JsonProperty("codigo")
            String code,
            @JsonProperty("nome")
            String name,
            @JsonProperty("descricao")
            String description,
            @JsonProperty("tipoItem")
            String itemType,
            @JsonProperty("unidadeMedida")
            String unitOfMeasure
    ) { }
}
