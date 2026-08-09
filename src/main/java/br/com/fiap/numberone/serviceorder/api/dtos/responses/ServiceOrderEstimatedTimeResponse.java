package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceOrderEstimatedTimeResponse(
        @JsonProperty("idOrdemServico")
        UUID serviceOrderId,
        @JsonProperty("tempoEstimadoTotalMinutos")
        Integer totalEstimatedMinutes,
        @JsonProperty("dataHoraPrevistaSugerida")
        LocalDateTime suggestedExpectedDateTime
) {
}
