package br.com.fiap.numberone.serviceorder.api.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateServiceOrderItemRequest(
        @JsonProperty("idServico")
        @NotNull(message = "idServico e obrigatorio")
        UUID serviceId,
        @JsonProperty("idOrdemServico")
        @NotNull(message = "idOrdemServico e obrigatorio")
        UUID serviceOrderId,
        @JsonProperty("valor")
        @Positive(message = "valor deve ser maior que zero")
        @NotNull(message = "valor e obrigatorio")
        BigDecimal value,
        @JsonProperty("opcional")
        Boolean optional
) { }
