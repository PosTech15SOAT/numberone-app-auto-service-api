package br.com.fiap.numberone.serviceorder.api.controllers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderItemRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderItemResponse;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderItemApiMapper;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/admin/itens-ordem-servico")
public class ServiceOrderItemController {

    private final ServiceOrderItemApiMapper serviceOrderItemApiMapper;

    private final ServiceOrderItemService serviceOrderItemService;

    public ServiceOrderItemController(
            ServiceOrderItemApiMapper serviceOrderItemApiMapper,
            ServiceOrderItemService serviceOrderItemService
    ) {
        this.serviceOrderItemApiMapper = serviceOrderItemApiMapper;
        this.serviceOrderItemService = serviceOrderItemService;
    }


    @PostMapping
    public ResponseEntity<ServiceOrderItemResponse> addServiceItem(
            @Valid @RequestBody CreateServiceOrderItemRequest createOrderItemRequest
    ) {
        ServiceOrderItem serviceOrderItem = serviceOrderItemService.createServiceOrderItem(serviceOrderItemApiMapper.toDomain(createOrderItemRequest));
        return ResponseEntity.ok(serviceOrderItemApiMapper.toResponse(serviceOrderItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceOrderItemResponse> removeServiceItem(
            @PathVariable UUID id
    ) {
        serviceOrderItemService.deleteServiceOrderItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<ServiceOrderItemResponse> startServiceOrderItem(
            @PathVariable UUID id
    ) {
        ServiceOrderItem serviceOrderItem = serviceOrderItemService.startServiceOrderItem(id);
        return ResponseEntity.ok(serviceOrderItemApiMapper.toResponse(serviceOrderItem));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ServiceOrderItemResponse> cancelServiceOrderItem(
            @PathVariable UUID id
    ) {
        ServiceOrderItem serviceOrderItem = serviceOrderItemService.cancelServiceOrderItem(id);
        return ResponseEntity.ok(serviceOrderItemApiMapper.toResponse(serviceOrderItem));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ServiceOrderItemResponse> completeServiceOrderItem(
            @PathVariable UUID id
    ) {
        ServiceOrderItem serviceOrderItem = serviceOrderItemService.completeServiceOrderItem(id);
        return ResponseEntity.ok(serviceOrderItemApiMapper.toResponse(serviceOrderItem));
    }


}
