package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.automotiveservice.infrastructure.persistence.repositories.AutoServiceRepository;
import br.com.fiap.numberone.serviceorder.application.gateways.AutomotiveServiceGateway;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.AutomotiveServiceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AutomotiveServiceGatewayImpl implements AutomotiveServiceGateway {

    private final AutoServiceRepository repository;
    private final AutomotiveServiceMapper mapper;

    public AutomotiveServiceGatewayImpl(AutoServiceRepository repository, AutomotiveServiceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<AutomotiveService> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
