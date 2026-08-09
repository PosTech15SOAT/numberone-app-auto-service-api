package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;

import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderBudgetGateway {

    ServiceOrderBudget save(ServiceOrderBudget serviceOrderBudget);

    Optional<ServiceOrderBudget> findById(UUID id);
}
