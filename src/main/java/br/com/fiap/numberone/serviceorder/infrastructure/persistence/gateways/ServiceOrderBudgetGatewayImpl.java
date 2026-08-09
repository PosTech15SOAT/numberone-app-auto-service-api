package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderBudgetEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderBudgetMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderBudgetRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class ServiceOrderBudgetGatewayImpl implements ServiceOrderBudgetGateway {

    private final ServiceOrderBudgetRepository repository;
    private final ServiceOrderBudgetMapper mapper;

    public ServiceOrderBudgetGatewayImpl(ServiceOrderBudgetRepository repository, ServiceOrderBudgetMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ServiceOrderBudget save(ServiceOrderBudget serviceOrderBudget) {
        ServiceOrderBudgetEntity entity = mapper.toEntity(serviceOrderBudget);
        ServiceOrderBudgetEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceOrderBudget> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
