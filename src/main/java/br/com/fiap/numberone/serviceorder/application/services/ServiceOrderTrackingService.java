package br.com.fiap.numberone.serviceorder.application.services;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;

import java.util.UUID;

public class ServiceOrderTrackingService {

    private final ServiceOrderGateway serviceOrderGateway;

    public ServiceOrderTrackingService(ServiceOrderGateway serviceOrderGateway) {
        this.serviceOrderGateway = serviceOrderGateway;
    }

    public ServiceOrder getTracking(UUID id) {
        return serviceOrderGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found for id: " + id));
    }
}
