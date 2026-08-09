package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderItemSupplyRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.requests.UpdateServiceOrderItemSupplyRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderItemSupplyResponse;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemSupplyUpdate;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceOrderItemSupplyApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceOrderItem", ignore = true)
    @Mapping(target = "inventoryItem.id", source = "inventoryItemId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceOrderItemSupply toDomain(CreateServiceOrderItemSupplyRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceOrderItem", ignore = true)
    @Mapping(target = "inventoryItem.id", source = "inventoryItemId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceOrderItemSupply toDomain(UpdateServiceOrderItemSupplyRequest dto);

    ServiceOrderItemSupplyUpdate toUpdate(UpdateServiceOrderItemSupplyRequest dto);

    @Mapping(target = "serviceOrderItemId", source = "serviceOrderItem.id")
    ServiceOrderItemSupplyResponse toResponse(ServiceOrderItemSupply entity);
}
