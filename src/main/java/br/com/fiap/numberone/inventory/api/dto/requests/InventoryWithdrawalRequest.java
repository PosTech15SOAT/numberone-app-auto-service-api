package br.com.fiap.numberone.inventory.api.dto.requests;

import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryWithdrawalRequest {

    @JsonProperty("idItemEstoque")
    @NotNull(message = "O campo idItemEstoque é obrigatório.")
    private UUID inventoryItemId;

    @JsonProperty("quantidade")
    @NotNull(message = "O campo quantidade é obrigatório.")
    @Min(value = 1, message = "O campo quantidade deve ser maior que zero.")
    private Integer quantity;

    @JsonProperty("origemMovimentacao")
    @NotNull(message = "O campo origemMovimentacao é obrigatório.")
    private InventoryMovementOrigin movementOrigin;

    @JsonProperty("referenciaOrigemId")
    private UUID originReferenceId;

    @JsonProperty("observacao")
    private String observation;

    @JsonProperty("usuarioResponsavelId")
    @NotNull(message = "O campo usuarioResponsavelId é obrigatório.")
    private UUID responsibleUserId;
}