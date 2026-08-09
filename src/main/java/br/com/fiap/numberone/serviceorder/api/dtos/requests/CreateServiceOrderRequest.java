package br.com.fiap.numberone.serviceorder.api.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateServiceOrderRequest(
        @JsonProperty("descricaoInicial")
        @NotBlank(message = "descricaoInicial e obrigatoria") String initialDescription,
        @JsonProperty("descricaoDiagnostico")
        @NotBlank(message = "descricaoDiagnostico e obrigatoria") String diagnosisDescription,
        @JsonProperty("observacao")
        String notes,
        @JsonProperty("idCliente")
        @NotNull(message = "idCliente e obrigatorio") UUID customerId,
        @JsonProperty("idVeiculo")
        @NotNull(message = "idVeiculo e obrigatorio") UUID vehicleId,
        @JsonProperty("dataHoraEntrada")
        @NotNull(message = "dataHoraEntrada e obrigatoria") LocalDateTime entryDateTime
) { }
