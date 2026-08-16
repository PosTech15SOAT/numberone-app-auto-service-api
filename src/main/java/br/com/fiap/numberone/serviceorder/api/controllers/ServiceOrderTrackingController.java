package br.com.fiap.numberone.serviceorder.api.controllers;

import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderTrackingResponse;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderTrackingApiMapper;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderTrackingService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.shared.security.infrastructure.authorization.AuthenticatedCustomerAccess;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/ordens-servico")
public class ServiceOrderTrackingController {

    private final ServiceOrderTrackingApiMapper serviceOrderTrackingApiMapper;
    private final ServiceOrderTrackingService serviceOrderTrackingService;
    private final AuthenticatedCustomerAccess authenticatedCustomerAccess;

    public ServiceOrderTrackingController(
            ServiceOrderTrackingApiMapper serviceOrderTrackingApiMapper,
            ServiceOrderTrackingService serviceOrderTrackingService,
            AuthenticatedCustomerAccess authenticatedCustomerAccess
    ) {
        this.serviceOrderTrackingApiMapper = serviceOrderTrackingApiMapper;
        this.serviceOrderTrackingService = serviceOrderTrackingService;
        this.authenticatedCustomerAccess = authenticatedCustomerAccess;
    }

    @GetMapping("/{id}/acompanhamento")
    public ResponseEntity<ServiceOrderTrackingResponse> getServiceOrderTracking(@PathVariable UUID id) {
        ServiceOrder serviceOrder = serviceOrderTrackingService.getTracking(id);
        UUID customerId = serviceOrder.getCustomer() == null ? null : serviceOrder.getCustomer().getId();
        authenticatedCustomerAccess.requireCurrentUserOwnershipOrAdmin(customerId);
        return ResponseEntity.ok(serviceOrderTrackingApiMapper.toResponse(serviceOrder));
    }

}
