package br.com.fiap.numberone.inventory.infrastructure.persistence.mappers;

import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryItemEntityMapper {

    default InventoryItem toDomain(InventoryItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return InventoryItem.restore(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getItemType(),
                entity.getUnitOfMeasure(),
                entity.getCostPerUnit(),
                entity.getSalePrice(),
                entity.getInventoryQuantity(),
                entity.getMinimumInventoryQuantity(),
                entity.getBrand(),
                entity.getApplicableVehicle(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    InventoryItemEntity toEntity(InventoryItem domain);
}