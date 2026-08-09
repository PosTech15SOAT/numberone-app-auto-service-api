package br.com.fiap.numberone.serviceorder.api.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateServiceOrderItemSupplyRequest(
        @JsonProperty("idItemEstoque")
        @NotNull(message = "idItemEstoque e obrigatorio")
        UUID inventoryItemId,
        @JsonProperty("quantidadeUsada")
        @NotNull(message = "quantidadeUsada e obrigatoria")
        @Positive(message = "quantidadeUsada deve ser maior que zero")
        Integer quantityUsed
) { }
