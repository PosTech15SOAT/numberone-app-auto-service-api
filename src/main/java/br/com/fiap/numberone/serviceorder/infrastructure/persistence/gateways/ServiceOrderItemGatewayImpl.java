package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemCompletionUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemStartUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderItemMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ServiceOrderItemGatewayImpl implements ServiceOrderItemGateway {

    @PersistenceContext
    private EntityManager entityManager;

    private final ServiceOrderItemRepository repository;
    private final ServiceOrderItemMapper mapper;

    public ServiceOrderItemGatewayImpl(ServiceOrderItemRepository repository, ServiceOrderItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ServiceOrderItem save(ServiceOrderItem serviceOrderItem) {
        ServiceOrderItemEntity entity = mapper.toEntity(serviceOrderItem);
        ServiceOrderItemEntity savedEntity = repository.saveAndFlush(entity);
        entityManager.clear();
        return repository.findById(savedEntity.getId())
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Saved service order item could not be reloaded"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceOrderItem> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceOrderItem> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public ServiceOrderItem updateStatus(UUID id, OrderItemStatus status) {
        repository.updateStatus(id, status);
        entityManager.clear();
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Updated service order item could not be reloaded"));
    }

    @Override
    @Transactional
    public ServiceOrderItem start(ServiceOrderItemStartUpdate update) {
        repository.start(update);
        entityManager.clear();
        return repository.findById(update.getServiceOrderItemId())
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Started service order item could not be reloaded"));
    }

    @Override
    @Transactional
    public ServiceOrderItem complete(ServiceOrderItemCompletionUpdate update) {
        repository.complete(update);
        entityManager.clear();
        return repository.findById(update.getServiceOrderItemId())
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Completed service order item could not be reloaded"));
    }
}
