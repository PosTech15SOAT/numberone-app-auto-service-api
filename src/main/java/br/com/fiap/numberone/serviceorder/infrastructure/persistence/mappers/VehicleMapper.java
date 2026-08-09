package br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers;

import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;
import br.com.fiap.numberone.vehicle.infrastructure.persistence.entities.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {
    VehicleEntity toEntity(Vehicle vehicle);

    Vehicle toDomain(VehicleEntity entity);
}
