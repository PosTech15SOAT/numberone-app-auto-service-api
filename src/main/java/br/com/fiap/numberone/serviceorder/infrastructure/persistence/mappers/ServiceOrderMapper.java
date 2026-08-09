package br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers;

import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { CustomerMapper.class, VehicleMapper.class, ServiceOrderItemMapper.class, ServiceOrderBudgetMapper.class }
)
public interface ServiceOrderMapper {

    @Mapping(target = "vehicleEntity", source = "vehicle")
    @Mapping(target = "items", source = "serviceItems")
    @Mapping(target = "budgets", source = "budgets")
    ServiceOrderEntity toEntity(ServiceOrder domain);

    @Mapping(target = "vehicle", source = "vehicleEntity")
    @Mapping(target = "serviceItems", source = "items")
    @Mapping(target = "budgets", source = "budgets")
    ServiceOrder toDomain(ServiceOrderEntity entity);

    @AfterMapping
    default void linkRelationships(@MappingTarget ServiceOrderEntity entity) {
        entity.linkChildren();
    }
}
