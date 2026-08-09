package br.com.fiap.numberone.inventory.api.mappers;

import br.com.fiap.numberone.inventory.api.dto.requests.InventoryItemRequest;
import br.com.fiap.numberone.inventory.api.dto.responses.InventoryItemResponse;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryItemApiMapper {

    default InventoryItem toDomain(InventoryItemRequest request) {
        if (request == null) {
            return null;
        }

        return InventoryItem.create(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getItemType(),
                request.getUnitOfMeasure(),
                request.getCostPerUnit(),
                request.getSalePrice(),
                request.getInventoryQuantity(),
                request.getMinimumInventoryQuantity(),
                request.getBrand(),
                request.getApplicableVehicle(),
                request.getActive()
        );
    }

    InventoryItemResponse toResponse(InventoryItem domain);
}