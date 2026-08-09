package br.com.fiap.numberone.inventory.infrastructure.persistence.mappers;

import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryMovementEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMovementEntityMapper {

    InventoryMovementEntity toEntity(InventoryMovement domain);

    default InventoryMovement toDomain(InventoryMovementEntity entity) {
        if (entity == null) {
            return null;
        }

        return InventoryMovement.restore(
                entity.getId(),
                entity.getInventoryItemId(),
                entity.getMovementType(),
                entity.getMovementOrigin(),
                entity.getOriginReferenceId(),
                entity.getQuantityBefore(),
                entity.getQuantityAfter(),
                entity.getObservation(),
                entity.getResponsibleUserId(),
                entity.getCreatedAt()
        );
    }
}