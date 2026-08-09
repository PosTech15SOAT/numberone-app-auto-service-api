package br.com.fiap.numberone.inventory.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("idItemEstoque")
    private UUID inventoryItemId;

    @JsonProperty("tipoMovimentacao")
    private String movementType;

    @JsonProperty("origemMovimentacao")
    private String movementOrigin;

    @JsonProperty("referenciaOrigemId")
    private UUID originReferenceId;

    @JsonProperty("quantidadeAntes")
    private Integer quantityBefore;

    @JsonProperty("quantidadeDepois")
    private Integer quantityAfter;

    @JsonProperty("observacao")
    private String observation;

    @JsonProperty("usuarioResponsavelId")
    private UUID responsibleUserId;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}