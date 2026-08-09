package br.com.fiap.numberone.vehicle.api.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record VehicleRequest(
        @JsonProperty("placa")
        @NotBlank(message = "Placa e obrigatoria")
        @Pattern(
                regexp = "^(?:[A-Za-z]{3}-\\d{4}|[A-Za-z]{3}\\d[A-Za-z]\\d{2})$",
                message = "Placa deve estar no formato antigo XXX-9999 ou Mercosul AAA0A00"
        )
        String licensePlate,

        @JsonProperty("marca")
        @NotBlank(message = "Marca e obrigatoria")
        String brand,

        @JsonProperty("modelo")
        @NotBlank(message = "Modelo e obrigatorio")
        String model,

        @JsonProperty("ano")
        @NotNull(message = "Ano e obrigatorio")
        Integer year,

        @JsonProperty("idCliente")
        @NotNull(message = "idCliente e obrigatorio")
        UUID customerId
) {
}
