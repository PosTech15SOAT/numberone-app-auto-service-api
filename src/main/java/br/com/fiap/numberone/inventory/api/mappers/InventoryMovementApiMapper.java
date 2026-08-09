package br.com.fiap.numberone.inventory.api.mappers;

import br.com.fiap.numberone.inventory.api.dto.responses.InventoryMovementResponse;
import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMovementApiMapper {

    InventoryMovementResponse toResponse(InventoryMovement domain);
}