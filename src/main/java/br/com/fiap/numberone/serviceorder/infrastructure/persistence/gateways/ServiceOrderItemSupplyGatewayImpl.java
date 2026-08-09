package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemSupplyGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemSupplyEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderItemSupplyMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderItemSupplyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ServiceOrderItemSupplyGatewayImpl implements ServiceOrderItemSupplyGateway {

    private final ServiceOrderItemSupplyRepository repository;
    private final ServiceOrderItemSupplyMapper mapper;

    public ServiceOrderItemSupplyGatewayImpl(
            ServiceOrderItemSupplyRepository repository,
            ServiceOrderItemSupplyMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ServiceOrderItemSupply save(ServiceOrderItemSupply serviceOrderItemSupply) {
        ServiceOrderItemSupplyEntity entity = mapper.toEntity(serviceOrderItemSupply);
        ServiceOrderItemSupplyEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceOrderItemSupply> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceOrderItemSupply> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceOrderItemSupply> findByServiceOrderItemId(UUID serviceOrderItemId) {
        return repository.findByServiceOrderItemId(serviceOrderItemId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
