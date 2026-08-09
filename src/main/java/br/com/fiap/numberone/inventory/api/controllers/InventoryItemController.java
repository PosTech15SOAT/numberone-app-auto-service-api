package br.com.fiap.numberone.inventory.api.controllers;

import br.com.fiap.numberone.inventory.api.dto.requests.InventoryItemRequest;
import br.com.fiap.numberone.inventory.api.dto.responses.InventoryItemResponse;
import br.com.fiap.numberone.inventory.api.mappers.InventoryItemApiMapper;
import br.com.fiap.numberone.inventory.application.services.InventoryItemService;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.shared.api.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ADMIN + "/itens")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;
    private final InventoryItemApiMapper inventoryItemApiMapper;

    public InventoryItemController(InventoryItemService inventoryItemService, InventoryItemApiMapper inventoryItemApiMapper1) {
        this.inventoryItemService = inventoryItemService;
        this.inventoryItemApiMapper = inventoryItemApiMapper1;
    }

    @PostMapping
    public ResponseEntity<InventoryItemResponse> create(@RequestBody @Valid InventoryItemRequest inventoryItemRequest) {
        InventoryItem inventoryItem = inventoryItemService.create(inventoryItemApiMapper.toDomain(inventoryItemRequest));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(inventoryItem.getId())
                .toUri();

        return ResponseEntity.created(location).body(inventoryItemApiMapper.toResponse(inventoryItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> update(@PathVariable UUID id,
                                  @RequestBody @Valid InventoryItemRequest inventoryItemRequest) {

        InventoryItem updatedInventoryItem = inventoryItemService.update(id, inventoryItemApiMapper.toDomain(inventoryItemRequest));
        return ResponseEntity.ok(inventoryItemApiMapper.toResponse(updatedInventoryItem));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> findAll() {
        return ResponseEntity.ok(inventoryItemService.findAll()
                .stream()
                .map(inventoryItemApiMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryItemApiMapper.toResponse(inventoryItemService.findById(id)));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inactivate(@PathVariable UUID id) {
        inventoryItemService.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        inventoryItemService.activate(id);
        return ResponseEntity.noContent().build();
    }
}
