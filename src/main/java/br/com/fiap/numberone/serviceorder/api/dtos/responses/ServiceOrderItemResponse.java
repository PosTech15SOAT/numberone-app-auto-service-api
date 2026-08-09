package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceOrderItemResponse(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("idOrdemServico")
        UUID serviceOrderId,
        @JsonProperty("servicoAutomotivo")
        AutomotiveServiceResponse automotiveService,
        @JsonProperty("valor")
        BigDecimal value,
        @JsonProperty("status")
        ServiceOrderItemStatusResponse status,
        @JsonProperty("opcional")
        Boolean optional,
        @JsonProperty("insumos")
        List<ServiceOrderItemSupplyResponse> supplies,
        @JsonProperty("dataHoraInicio")
        LocalDateTime startDateTime,
        @JsonProperty("dataHoraFim")
        LocalDateTime endDateTime,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
    public record AutomotiveServiceResponse (
            @JsonProperty("id")
            UUID id,
            @JsonProperty("codigo")
            String code,
            @JsonProperty("nome")
            String name,
            @JsonProperty("descricao")
            String description,
            @JsonProperty("tipoServico")
            String serviceType,
            @JsonProperty("valorBase")
            BigDecimal baseValue,
            @JsonProperty("tempoEstimadoMinutos")
            Integer estimatedTimeMinutes,
            @JsonProperty("ativo")
            Boolean active
    ) { }
}
