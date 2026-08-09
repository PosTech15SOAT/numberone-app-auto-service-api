package br.com.fiap.numberone.serviceorder.api.dtos.responses;

import br.com.fiap.numberone.customer.domain.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceOrderResponse(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("descricaoInicial")
        String initialDescription,
        @JsonProperty("descricaoDiagnostico")
        String diagnosisDescription,
        @JsonProperty("descricaoDiagnosticoFinal")
        String finalDiagnosisDescription,
        @JsonProperty("observacao")
        String notes,
        @JsonProperty("cliente")
        CustomerResponse customer,
        @JsonProperty("veiculo")
        VehicleResponse vehicle,
        @JsonProperty("itensServico")
        List<ServiceOrderItemResponse> serviceItems,
        @JsonProperty("orcamentos")
        List<ServiceOrderBudgetResponse> budgets,
        @JsonProperty("status")
        ServiceOrderStatusResponse status,
        @JsonProperty("dataHoraEntrada")
        LocalDateTime entryDateTime,
        @JsonProperty("dataHoraPrevista")
        LocalDateTime expectedDateTime,
        @JsonProperty("dataHoraEntrega")
        LocalDateTime deliveryDateTime,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
    public record CustomerResponse(
            @JsonProperty("id")
            UUID id,
            @JsonProperty("nome")
            String name,
            @JsonProperty("tipoDocumento")
            TipoDocumento documentType,
            @JsonProperty("documento")
            String document,
            @JsonProperty("email")
            String email,
            @JsonProperty("telefone")
            String phone,
            @JsonProperty("endereco")
            String address,
            @JsonProperty("ativo")
            Boolean active
    ) { }

    public record VehicleResponse(
            @JsonProperty("id")
            UUID id,
            @JsonProperty("placa")
            String licensePlate,
            @JsonProperty("marca")
            String brand,
            @JsonProperty("modelo")
            String model,
            @JsonProperty("ano")
            Integer year,
            @JsonProperty("idCliente")
            UUID customerId,
            @JsonProperty("created_at")
            LocalDateTime createdAt,
            @JsonProperty("updated_at")
            LocalDateTime updatedAt
    ) { }
}


