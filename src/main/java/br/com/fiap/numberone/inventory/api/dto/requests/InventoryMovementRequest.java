package br.com.fiap.numberone.inventory.api.dto.requests;

import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementRequest {

    @JsonProperty("inventoryItemId")
    @NotNull(message = "O campo inventoryItemId é obrigatório.")
    private UUID inventoryItemId;

    @JsonProperty("tipoMovimentacao")
    @NotNull(message = "O campo tipoMovimentacao é obrigatório.")
    private InventoryMovementType movementType;

    @JsonProperty("origemMovimentacao")
    @NotNull(message = "O campo origemMovimentacao é obrigatório.")
    private InventoryMovementOrigin movementOrigin;

    @JsonProperty("referenciaOrigemId")
    private UUID originReferenceId;

    @JsonProperty("quantidade")
    @NotNull(message = "O campo quantidade é obrigatório.")
    @Min(value = 1, message = "O campo quantidade deve ser maior que zero.")
    private Integer quantity;

    @JsonProperty("observacao")
    @NotBlank(message = "O campo observacao é obrigatório.")
    @Size(max = 500, message = "O campo observacao deve ter no máximo 500 caracteres.")
    private String observation;

    @JsonProperty("usuarioResponsavelId")
    private UUID responsibleUserId;
}