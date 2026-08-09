package br.com.fiap.numberone.serviceorder.api.controllers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.requests.FinalDiagnosisRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderAverageExecutionTimeResponse;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderEstimatedTimeResponse;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderResponse;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderValueResponse;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderApiMapper;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderAverageExecutionTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderEstimatedTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderValue;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ordens-servico")
public class ServiceOrderController {

    private final ServiceOrderApiMapper orderApiMapper;
    private final ServiceOrderService serviceOrderService;

    public ServiceOrderController(
            ServiceOrderApiMapper orderApiMapper,
            ServiceOrderService serviceOrderService
    ) {
        this.orderApiMapper = orderApiMapper;
        this.serviceOrderService = serviceOrderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponse> getServiceOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrderService.getServiceOrder(id)));
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderResponse>> getServiceOrders() {
        return ResponseEntity.ok(serviceOrderService.getServiceOrders()
                .stream()
                .map(orderApiMapper::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<ServiceOrderResponse> createServiceOrder(
            @Valid @RequestBody CreateServiceOrderRequest createServiceOrderRequest
    ) {
        ServiceOrder serviceOrder = serviceOrderService.createServiceOrder(orderApiMapper.toDomain(createServiceOrderRequest));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(serviceOrder.getId())
                .toUri();
        return ResponseEntity.created(location).body(orderApiMapper.toResponse(serviceOrder));
    }

    @PatchMapping("/{id}/iniciar-diagnostico")
    public ResponseEntity<ServiceOrderResponse> addFinalDiagnosis(
            @PathVariable UUID id,
            @Valid @RequestBody FinalDiagnosisRequest finalDiagnosisRequest
    ) {
        ServiceOrder serviceOrder = serviceOrderService.addFinalDiagnosis(id, orderApiMapper.toDomain(finalDiagnosisRequest));
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrder));
    }

    @GetMapping("/{id}/calcular-servicos")
    public ResponseEntity<ServiceOrderValueResponse> calculateServices(@PathVariable UUID id) {
        ServiceOrderValue serviceOrderValue = serviceOrderService.calculateServices(id);
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrderValue));
    }

    @GetMapping("/{id}/calcular-tempo-estimado")
    public ResponseEntity<ServiceOrderEstimatedTimeResponse> calculateEstimatedTime(@PathVariable UUID id) {
        ServiceOrderEstimatedTime serviceOrderEstimatedTime = serviceOrderService.calculateEstimatedTime(id);
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrderEstimatedTime));
    }

    @GetMapping("/{id}/tempo-medio-execucao-servicos")
    public ResponseEntity<ServiceOrderAverageExecutionTimeResponse> calculateAverageServiceExecutionTime(@PathVariable UUID id) {
        ServiceOrderAverageExecutionTime serviceOrderAverageExecutionTime = serviceOrderService.calculateAverageServiceExecutionTime(id);
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrderAverageExecutionTime));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ServiceOrderResponse> cancelServiceOrder(
            @PathVariable UUID id
    ) {
        ServiceOrder serviceOrder = serviceOrderService.cancelOrderService(id);
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrder));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<ServiceOrderResponse> startServiceOrder(
            @PathVariable UUID id
    ) {
        ServiceOrder serviceOrder = serviceOrderService.startOrderService(id);
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrder));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ServiceOrderResponse> completeServiceOrder(
            @PathVariable UUID id
    ) {
        ServiceOrder serviceOrder = serviceOrderService.completeOrderService(id);
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrder));
    }

    @PatchMapping("/{id}/entregar")
    public ResponseEntity<ServiceOrderResponse> deliverServiceOrder(
            @PathVariable UUID id
    ) {
        ServiceOrder serviceOrder = serviceOrderService.deliverOrderService(id);
        return ResponseEntity.ok(orderApiMapper.toResponse(serviceOrder));
    }
}
