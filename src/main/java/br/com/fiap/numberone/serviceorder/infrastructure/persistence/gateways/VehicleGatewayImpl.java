package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.serviceorder.application.gateways.VehicleGateway;
import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.VehicleMapper;
import br.com.fiap.numberone.vehicle.infrastructure.persistence.repositories.VehicleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class VehicleGatewayImpl implements VehicleGateway {

    private final VehicleRepository repository;
    private final VehicleMapper mapper;

    public VehicleGatewayImpl(VehicleRepository repository, VehicleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
