package br.com.fiap.numberone.serviceorder.api.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FinalDiagnosisRequest(
        @JsonProperty("descricaoDiagnosticoFinal")
        @NotBlank(message = "descricaoDiagnosticoFinal e obrigatoria") String finalDiagnosisDescription,
        @JsonProperty("dataHoraPrevista")
        @NotNull(message = "dataHoraPrevista e obrigatoria") LocalDateTime expectedDateTime,
        @JsonProperty("observacao")
        String notes
) { }
