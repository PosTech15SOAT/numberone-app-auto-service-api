package br.com.fiap.numberone.serviceorder.api.controllers;

import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderTrackingResponse;
import br.com.fiap.numberone.serviceorder.api.mappers.ServiceOrderTrackingApiMapper;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderTrackingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/ordens-servico")
public class ServiceOrderTrackingController {

    private final ServiceOrderTrackingApiMapper serviceOrderTrackingApiMapper;
    private final ServiceOrderTrackingService serviceOrderTrackingService;

    public ServiceOrderTrackingController(
            ServiceOrderTrackingApiMapper serviceOrderTrackingApiMapper,
            ServiceOrderTrackingService serviceOrderTrackingService
    ) {
        this.serviceOrderTrackingApiMapper = serviceOrderTrackingApiMapper;
        this.serviceOrderTrackingService = serviceOrderTrackingService;
    }

    @GetMapping("/{id}/acompanhamento")
    public ResponseEntity<ServiceOrderTrackingResponse> getServiceOrderTracking(@PathVariable UUID id) {
        return ResponseEntity.ok(
                serviceOrderTrackingApiMapper.toResponse(serviceOrderTrackingService.getTracking(id))
        );
    }

}
