package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderDeliveryUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderFinalDiagnosisUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ServiceOrderGatewayImpl implements ServiceOrderGateway {

    private final ServiceOrderRepository repository;
    private final ServiceOrderMapper mapper;

    public ServiceOrderGatewayImpl(ServiceOrderRepository repository, ServiceOrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ServiceOrder save(ServiceOrder serviceOrder) {
        ServiceOrderEntity entity = mapper.toEntity(serviceOrder);
        ServiceOrderEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceOrder> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceOrder> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public ServiceOrder updateStatus(UUID id, ServiceOrderStatus status) {
        repository.updateStatus(id, status);
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Updated service order could not be reloaded"));
    }

    @Override
    @Transactional
    public ServiceOrder updateFinalDiagnosis(ServiceOrderFinalDiagnosisUpdate update) {
        repository.updateFinalDiagnosis(update);
        return repository.findById(update.getServiceOrderId())
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Updated service order could not be reloaded"));
    }

    @Override
    @Transactional
    public ServiceOrder deliver(ServiceOrderDeliveryUpdate update) {
        repository.deliver(update);
        return repository.findById(update.getServiceOrderId())
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Updated service order could not be reloaded"));
    }
}
