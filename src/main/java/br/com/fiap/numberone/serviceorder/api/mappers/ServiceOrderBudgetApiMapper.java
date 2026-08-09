package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderBudgetRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderBudgetResponse;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ServiceOrderBudgetStatusApiMapper.class)
public interface ServiceOrderBudgetApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "approvedAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceOrderBudget toDomain(CreateServiceOrderBudgetRequest dto);

    @Mapping(target = "serviceOrderId", source = "serviceOrder.id")
    ServiceOrderBudgetResponse toResponse(ServiceOrderBudget entity);
}
