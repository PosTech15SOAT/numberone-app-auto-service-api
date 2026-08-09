package br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers;

import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderBudgetEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceOrderBudgetMapper {

    @Mapping(target = "serviceOrder", source = "serviceOrder", qualifiedByName = "toServiceOrderEntityRef")
    ServiceOrderBudgetEntity toEntity(ServiceOrderBudget domain);

    @Mapping(target = "serviceOrder", source = "serviceOrder", qualifiedByName = "toServiceOrderRef")
    ServiceOrderBudget toDomain(ServiceOrderBudgetEntity entity);

    @Named("toServiceOrderRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceOrder toServiceOrderRef(ServiceOrderEntity entity);

    @Named("toServiceOrderEntityRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceOrderEntity toServiceOrderEntityRef(ServiceOrder domain);
}
