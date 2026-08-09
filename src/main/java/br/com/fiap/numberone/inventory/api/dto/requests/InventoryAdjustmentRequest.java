package br.com.fiap.numberone.inventory.api.dto.requests;

import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAdjustmentRequest {

    @JsonProperty("idItemEstoque")
    @NotNull(message = "O campo idItemEstoque é obrigatório.")
    private UUID inventoryItemId;

    @JsonProperty("quantidadeFinal")
    @NotNull(message = "O campo quantidadeFinal é obrigatório.")
    @Min(value = 0, message = "O campo quantidadeFinal não pode ser negativo.")
    private Integer finalQuantity;

    @JsonProperty("origemMovimentacao")
    @NotNull(message = "O campo origemMovimentacao é obrigatório.")
    private InventoryMovementOrigin movementOrigin;

    @JsonProperty("referenciaOrigemId")
    private UUID originReferenceId;

    @JsonProperty("observacao")
    @NotBlank(message = "O campo observacao é obrigatório.")
    private String observation;

    @JsonProperty("usuarioResponsavelId")
    @NotNull(message = "O campo usuarioResponsavelId é obrigatório.")
    private UUID responsibleUserId;
}