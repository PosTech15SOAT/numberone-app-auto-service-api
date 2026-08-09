package br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers;

import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemSupplyEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { ServiceOrderItemMapper.class, InventoryItemMapper.class }
)
public interface ServiceOrderItemSupplyMapper {

    @Mapping(target = "serviceOrderItem", source = "serviceOrderItem", qualifiedByName = "toServiceOrderItemEntityRef")
    @Mapping(target = "inventoryItemEntity", source = "inventoryItem")
    ServiceOrderItemSupplyEntity toEntity(ServiceOrderItemSupply domain);

    @Mapping(target = "serviceOrderItem", source = "serviceOrderItem", qualifiedByName = "toServiceOrderItemRef")
    @Mapping(target = "inventoryItem", source = "inventoryItemEntity")
    ServiceOrderItemSupply toDomain(ServiceOrderItemSupplyEntity entity);

    @Named("toServiceOrderItemRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status")
    ServiceOrderItem toServiceOrderItemRef(ServiceOrderItemEntity entity);

    @Named("toServiceOrderItemEntityRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceOrderItemEntity toServiceOrderItemEntityRef(ServiceOrderItem domain);
}
