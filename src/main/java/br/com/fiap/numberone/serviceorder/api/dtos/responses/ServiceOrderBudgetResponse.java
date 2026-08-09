package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceOrderBudgetResponse(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("idOrdemServico")
        UUID serviceOrderId,
        @JsonProperty("valorProposto")
        BigDecimal quotedAmount,
        @JsonProperty("valorAprovado")
        BigDecimal approvedAmount,
        @JsonProperty("status")
        ServiceOrderBudgetStatusResponse status,
        @JsonProperty("enviadoEm")
        LocalDateTime sentAt,
        @JsonProperty("aprovadoEm")
        LocalDateTime approvedAt,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
}
