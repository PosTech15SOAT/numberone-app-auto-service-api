package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceOrderTrackingResponse(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("descricaoInicial")
        String initialDescription,
        @JsonProperty("descricaoDiagnosticoFinal")
        String finalDiagnosisDescription,
        @JsonProperty("veiculo")
        VehicleResponse vehicle,
        @JsonProperty("status")
        ServiceOrderStatusResponse status,
        @JsonProperty("dataHoraEntrada")
        LocalDateTime entryDateTime,
        @JsonProperty("dataHoraPrevista")
        LocalDateTime expectedDateTime,
        @JsonProperty("dataHoraEntrega")
        LocalDateTime deliveryDateTime,
        @JsonProperty("orcamento")
        BudgetResponse budget,
        @JsonProperty("itensServico")
        List<ServiceItemResponse> serviceItems
) {

    public record VehicleResponse(
            @JsonProperty("placa")
            String licensePlate,
            @JsonProperty("marca")
            String brand,
            @JsonProperty("modelo")
            String model,
            @JsonProperty("ano")
            Integer year
    ) {
    }

    public record BudgetResponse(
            @JsonProperty("valorProposto")
            BigDecimal quotedAmount,
            @JsonProperty("valorAprovado")
            BigDecimal approvedAmount,
            @JsonProperty("status")
            ServiceOrderBudgetStatusResponse status,
            @JsonProperty("enviadoEm")
            LocalDateTime sentAt,
            @JsonProperty("aprovadoEm")
            LocalDateTime approvedAt
    ) {
    }

    public record ServiceItemResponse(
            @JsonProperty("id")
            UUID id,
            @JsonProperty("nomeServico")
            String serviceName,
            @JsonProperty("tipoServico")
            String serviceType,
            @JsonProperty("status")
            ServiceOrderItemStatusResponse status,
            @JsonProperty("opcional")
            Boolean optional,
            @JsonProperty("dataHoraInicio")
            LocalDateTime startDateTime,
            @JsonProperty("dataHoraFim")
            LocalDateTime endDateTime
    ) {
    }
}
