package br.com.fiap.numberone.serviceorder.api.controllers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderItemSupplyRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.requests.UpdateServiceOrderItemSupplyRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderItemSupplyResponse;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderItemSupplyApiMapper;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemSupplyService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos")
public class ServiceOrderItemSupplyController {

    private final ServiceOrderItemSupplyApiMapper supplyApiMapper;
    private final ServiceOrderItemSupplyService supplyService;

    public ServiceOrderItemSupplyController(
            ServiceOrderItemSupplyApiMapper supplyApiMapper,
            ServiceOrderItemSupplyService supplyService
    ) {
        this.supplyApiMapper = supplyApiMapper;
        this.supplyService = supplyService;
    }

    @PostMapping
    public ResponseEntity<ServiceOrderItemSupplyResponse> createServiceOrderItemSupply(
            @PathVariable UUID serviceOrderItemId,
            @Valid @RequestBody CreateServiceOrderItemSupplyRequest createServiceOrderItemSupplyRequest
    ) {
        ServiceOrderItemSupply serviceOrderItemSupply = supplyApiMapper.toDomain(createServiceOrderItemSupplyRequest);
        serviceOrderItemSupply.attachServiceOrderItem(
                ServiceOrderItem.builder()
                        .id(serviceOrderItemId)
                        .build()
        );
        serviceOrderItemSupply = supplyService.createItemSupply(serviceOrderItemSupply);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(serviceOrderItemSupply.getId())
                .toUri();
        return ResponseEntity.created(location).body(supplyApiMapper.toResponse(serviceOrderItemSupply));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOrderItemSupplyResponse> updateServiceOrderItemSupply(
            @PathVariable UUID serviceOrderItemId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceOrderItemSupplyRequest updateServiceOrderItemSupplyRequest
    ) {
        ServiceOrderItemSupply serviceOrderItemSupply = supplyService.updateItemSupply(
                id, supplyApiMapper.toUpdate(updateServiceOrderItemSupplyRequest)
        );
        return ResponseEntity.ok(supplyApiMapper.toResponse(serviceOrderItemSupply));
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderItemSupplyResponse>> listServiceOrderItemSupplies(
            @PathVariable UUID serviceOrderItemId
    ) {
        return ResponseEntity.ok(
                supplyService.listItemSupply(serviceOrderItemId)
                        .stream()
                        .map(supplyApiMapper::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceOrderItemSupply(
            @PathVariable UUID serviceOrderItemId,
            @PathVariable UUID id
    ) {
        supplyService.deleteServiceOrderItemSupply(id);
        return ResponseEntity.noContent().build();
    }
}
