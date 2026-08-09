package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ServiceOrderAverageExecutionTimeResponse(
        @JsonProperty("idOrdemServico")
        UUID serviceOrderId,
        @JsonProperty("servicosConcluidos")
        Integer completedServices,
        @JsonProperty("servicosPendentes")
        Integer pendingServices,
        @JsonProperty("servicosEmExecucao")
        Integer inProgressServices,
        @JsonProperty("servicosCancelados")
        Integer cancelledServices,
        @JsonProperty("servicosAguardandoPecasInsumos")
        Integer waitingServices,
        @JsonProperty("tempoMedioExecucaoMinutos")
        Long averageExecutionMinutes
) {
}
