package br.com.fiap.numberone.serviceorder.api.controllers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderBudgetRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderBudgetResponse;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderBudgetApiMapper;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderBudgetService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ServiceOrderBudgetController {

    private final ServiceOrderBudgetApiMapper budgetApiMapper;
    private final ServiceOrderBudgetService budgetService;

    public ServiceOrderBudgetController(
            ServiceOrderBudgetApiMapper budgetApiMapper,
            ServiceOrderBudgetService budgetService
    ) {
        this.budgetApiMapper = budgetApiMapper;
        this.budgetService = budgetService;
    }

    @PostMapping("/admin/ordens-servico/{serviceOrderId}/orcamentos")
    public ResponseEntity<ServiceOrderBudgetResponse> createServiceOrderBudget(
            @PathVariable UUID serviceOrderId,
            @Valid @RequestBody CreateServiceOrderBudgetRequest createServiceOrderBudgetRequest
    ) {
        ServiceOrderBudget serviceOrderBudget = ServiceOrderBudget.builder()
                .serviceOrder(ServiceOrder.builder().id(serviceOrderId).build())
                .quotedAmount(createServiceOrderBudgetRequest.quotedAmount())
                .build();
        serviceOrderBudget = budgetService.createDraftBudget(serviceOrderBudget);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/api/admin/orcamentos-ordem-servico/{id}")
                .buildAndExpand(serviceOrderBudget.getId())
                .toUri();
        return ResponseEntity.created(location).body(budgetApiMapper.toResponse(serviceOrderBudget));
    }

    @PatchMapping("/admin/orcamentos-ordem-servico/{id}/solicitar-aprovacao")
    public ResponseEntity<ServiceOrderBudgetResponse> requestApprovalBudget(@PathVariable UUID id) {
        ServiceOrderBudget serviceOrderBudget = budgetService.requestApproval(id);
        return ResponseEntity.ok(budgetApiMapper.toResponse(serviceOrderBudget));
    }

    @PatchMapping("/admin/orcamentos-ordem-servico/{id}/aprovar")
    public ResponseEntity<ServiceOrderBudgetResponse> approveBudget(@PathVariable UUID id) {
        ServiceOrderBudget serviceOrderBudget = budgetService.approve(id);
        return ResponseEntity.ok(budgetApiMapper.toResponse(serviceOrderBudget));
    }

    @PatchMapping("/admin/orcamentos-ordem-servico/{id}/rejeitar")
    public ResponseEntity<ServiceOrderBudgetResponse> rejectBudget(@PathVariable UUID id) {
        ServiceOrderBudget serviceOrderBudget = budgetService.reject(id);
        return ResponseEntity.ok(budgetApiMapper.toResponse(serviceOrderBudget));
    }

    @GetMapping("/public/orcamentos-ordem-servico/{id}/aprovacao/aprovar")
    public ResponseEntity<String> approveBudgetByEmailLink(@PathVariable UUID id) {
        budgetService.approve(id);
        return ResponseEntity.ok("Orcamento aprovado com sucesso.");
    }

    @GetMapping("/public/orcamentos-ordem-servico/{id}/aprovacao/rejeitar")
    public ResponseEntity<String> rejectBudgetByEmailLink(@PathVariable UUID id) {
        budgetService.reject(id);
        return ResponseEntity.ok("Orcamento rejeitado com sucesso.");
    }
}
