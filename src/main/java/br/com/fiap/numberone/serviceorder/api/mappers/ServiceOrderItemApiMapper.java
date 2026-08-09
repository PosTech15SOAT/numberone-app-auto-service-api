package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderItemRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderItemResponse;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ServiceOrderItemStatusApiMapper.class)
public interface ServiceOrderItemApiMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceOrder.id", source = "serviceOrderId")
    @Mapping(target = "automotiveService.id", source = "serviceId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceOrderItem toDomain(CreateServiceOrderItemRequest dto);

    @Mapping(target = "serviceOrderId", source = "serviceOrder.id")
    ServiceOrderItemResponse toResponse(ServiceOrderItem entity);
}
