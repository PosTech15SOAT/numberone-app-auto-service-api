package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceOrderValueResponse(
        @JsonProperty("idOrdemServico")
        UUID serviceOrderId,
        @JsonProperty("valorTotal")
        BigDecimal totalValue
) {

}
