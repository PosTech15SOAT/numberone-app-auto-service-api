package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderItemSupplyGateway {

    ServiceOrderItemSupply save(ServiceOrderItemSupply serviceOrderItemSupply);

    Optional<ServiceOrderItemSupply> findById(UUID id);

    List<ServiceOrderItemSupply> findAll();

    List<ServiceOrderItemSupply> findByServiceOrderItemId(UUID serviceOrderItemId);

    void deleteById(UUID id);
}
