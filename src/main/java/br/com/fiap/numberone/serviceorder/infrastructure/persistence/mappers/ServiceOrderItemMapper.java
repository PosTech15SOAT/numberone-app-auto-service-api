package br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers;

import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.serviceorder.domain.references.InventoryItem;
import br.com.fiap.numberone.automotiveservice.infrastructure.persistence.entities.AutomotiveServiceEntity;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryItemEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemEntity;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemSupplyEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceOrderItemMapper {

    @Mapping(target = "serviceOrder", source = "serviceOrder", qualifiedByName = "toServiceOrderEntityRef")
    @Mapping(target = "automotiveService", source = "automotiveService", qualifiedByName = "toAutomotiveServiceEntityRef")
    @Mapping(target = "supplies", source = "supplies", qualifiedByName = "toSupplyEntityRefList")
    ServiceOrderItemEntity toEntity(ServiceOrderItem domain);

    @Mapping(target = "serviceOrder", source = "serviceOrder", qualifiedByName = "toServiceOrderRef")
    @Mapping(target = "automotiveService", source = "automotiveService", qualifiedByName = "toAutomotiveServiceRef")
    @Mapping(target = "supplies", source = "supplies", qualifiedByName = "toSupplyRefList")
    ServiceOrderItem toDomain(ServiceOrderItemEntity entity);

    @Named("toServiceOrderRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceOrder toServiceOrderRef(ServiceOrderEntity entity);

    @Named("toServiceOrderEntityRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ServiceOrderEntity toServiceOrderEntityRef(ServiceOrder domain);

    @Named("toAutomotiveServiceRef")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "serviceType", source = "serviceType")
    @Mapping(target = "baseValue", source = "baseValue")
    @Mapping(target = "estimatedTimeMinutes", source = "estimatedTimeMinutes")
    @Mapping(target = "active", source = "active")
    AutomotiveService toAutomotiveServiceRef(AutomotiveServiceEntity entity);

    @Named("toAutomotiveServiceEntityRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AutomotiveServiceEntity toAutomotiveServiceEntityRef(AutomotiveService domain);

    @Named("toSupplyRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "quantityUsed", source = "quantityUsed")
    @Mapping(target = "inventoryItem", source = "inventoryItemEntity", qualifiedByName = "toInventoryItemRef")
    ServiceOrderItemSupply toSupplyRef(ServiceOrderItemSupplyEntity entity);

    @Named("toSupplyEntityRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "quantityUsed", source = "quantityUsed")
    @Mapping(target = "inventoryItemEntity", source = "inventoryItem", qualifiedByName = "toInventoryItemEntityRef")
    @Mapping(target = "serviceOrderItem", ignore = true)
    ServiceOrderItemSupplyEntity toSupplyEntityRef(ServiceOrderItemSupply domain);

    @Named("toInventoryItemRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "itemType", source = "itemType")
    @Mapping(target = "unitOfMeasure", source = "unitOfMeasure")
    @Mapping(target = "costPerUnit", source = "costPerUnit")
    @Mapping(target = "salePrice", source = "salePrice")
    @Mapping(target = "inventoryQuantity", source = "inventoryQuantity")
    @Mapping(target = "minimumInventoryQuantity", source = "minimumInventoryQuantity")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "applicableVehicle", source = "applicableVehicle")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    InventoryItem toInventoryItemRef(InventoryItemEntity entity);

    @Named("toInventoryItemEntityRef")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "itemType", source = "itemType")
    @Mapping(target = "unitOfMeasure", source = "unitOfMeasure")
    @Mapping(target = "costPerUnit", source = "costPerUnit")
    @Mapping(target = "salePrice", source = "salePrice")
    @Mapping(target = "inventoryQuantity", source = "inventoryQuantity")
    @Mapping(target = "minimumInventoryQuantity", source = "minimumInventoryQuantity")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "applicableVehicle", source = "applicableVehicle")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    InventoryItemEntity toInventoryItemEntityRef(InventoryItem domain);

    @Named("toSupplyRefList")
    @IterableMapping(qualifiedByName = "toSupplyRef")
    List<ServiceOrderItemSupply> toSupplyRefList(List<ServiceOrderItemSupplyEntity> entities);

    @Named("toSupplyEntityRefList")
    @IterableMapping(qualifiedByName = "toSupplyEntityRef")
    List<ServiceOrderItemSupplyEntity> toSupplyEntityRefList(List<ServiceOrderItemSupply> domains);
}
