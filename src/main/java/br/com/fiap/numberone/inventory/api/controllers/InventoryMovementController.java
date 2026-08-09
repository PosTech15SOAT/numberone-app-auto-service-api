package br.com.fiap.numberone.inventory.api.controllers;

import br.com.fiap.numberone.inventory.api.dto.requests.InventoryAdjustmentRequest;
import br.com.fiap.numberone.inventory.api.dto.requests.InventoryEntryRequest;
import br.com.fiap.numberone.inventory.api.dto.requests.InventoryWithdrawalRequest;
import br.com.fiap.numberone.inventory.api.dto.responses.InventoryMovementResponse;
import br.com.fiap.numberone.inventory.api.mappers.InventoryMovementApiMapper;
import br.com.fiap.numberone.inventory.application.services.InventoryMovementService;
import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.shared.api.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ADMIN + "/estoque")
public class InventoryMovementController {

    private final InventoryMovementService inventoryMovementService;
    private final InventoryMovementApiMapper inventoryMovementApiMapper;

    public InventoryMovementController(
            InventoryMovementService inventoryMovementService,
            InventoryMovementApiMapper inventoryMovementApiMapper
    ) {
        this.inventoryMovementService = inventoryMovementService;
        this.inventoryMovementApiMapper = inventoryMovementApiMapper;
    }

    @PostMapping("/entrada")
    public ResponseEntity<InventoryMovementResponse> registerEntry(
            @RequestBody @Valid InventoryEntryRequest request
    ) {
        InventoryMovement movement = inventoryMovementService.registerEntry(
                request.getInventoryItemId(),
                request.getQuantity(),
                request.getMovementOrigin(),
                request.getOriginReferenceId(),
                request.getObservation(),
                request.getResponsibleUserId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryMovementApiMapper.toResponse(movement));
    }

    @PostMapping("/baixa")
    public ResponseEntity<InventoryMovementResponse> registerWithdrawal(
            @RequestBody @Valid InventoryWithdrawalRequest request
    ) {
        InventoryMovement movement = inventoryMovementService.registerWithdrawal(
                request.getInventoryItemId(),
                request.getQuantity(),
                request.getMovementOrigin(),
                request.getOriginReferenceId(),
                request.getObservation(),
                request.getResponsibleUserId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryMovementApiMapper.toResponse(movement));
    }

    @PostMapping("/ajuste")
    public ResponseEntity<InventoryMovementResponse> registerAdjustment(
            @RequestBody @Valid InventoryAdjustmentRequest request
    ) {
        InventoryMovement movement = inventoryMovementService.registerAdjustment(
                request.getInventoryItemId(),
                request.getFinalQuantity(),
                request.getMovementOrigin(),
                request.getOriginReferenceId(),
                request.getObservation(),
                request.getResponsibleUserId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryMovementApiMapper.toResponse(movement));
    }

    @GetMapping("/itens/{itemId}/movimentacoes")
    public ResponseEntity<List<InventoryMovementResponse>> findByInventoryItemId(
            @PathVariable UUID itemId
    ) {
        return ResponseEntity.ok(
                inventoryMovementService.findByInventoryItemId(itemId)
                        .stream()
                        .map(inventoryMovementApiMapper::toResponse)
                        .toList()
        );
    }
}
