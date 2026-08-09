package br.com.fiap.numberone.inventory.unit.api;

import br.com.fiap.numberone.inventory.api.dto.responses.InventoryItemResponse;
import br.com.fiap.numberone.inventory.api.dto.responses.InventoryMovementResponse;
import br.com.fiap.numberone.inventory.api.mappers.InventoryItemApiMapper;
import br.com.fiap.numberone.inventory.api.mappers.InventoryMovementApiMapper;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementType.ENTRADA;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.entryMovement;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.inventoryItem;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.inventoryItemRequest;
import static org.assertj.core.api.Assertions.assertThat;

class InventoryApiMapperTest {

    private final InventoryItemApiMapper itemMapper = Mappers.getMapper(InventoryItemApiMapper.class);
    private final InventoryMovementApiMapper movementMapper = Mappers.getMapper(InventoryMovementApiMapper.class);

    @Test
    void shouldMapInventoryItemRequestToDomain() {
        // Given / When
        InventoryItem result = itemMapper.toDomain(inventoryItemRequest());

        // Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCode()).isEqualTo("OLEO-001");
        assertThat(result.getName()).isEqualTo("Oleo de motor");
        assertThat(result.getCostPerUnit()).isEqualByComparingTo("45.90");
        assertThat(result.getSalePrice()).isEqualByComparingTo("79.90");
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void shouldReturnNullWhenMappingNullInventoryItemRequest() {
        // Given / When / Then
        assertThat(itemMapper.toDomain(null)).isNull();
    }

    @Test
    void shouldMapInventoryItemDomainToResponse() {
        // Given / When
        InventoryItemResponse response = itemMapper.toResponse(inventoryItem());

        // Then
        assertThat(response.getId()).isEqualTo(inventoryItem().getId());
        assertThat(response.getCode()).isEqualTo("OLEO-001");
        assertThat(response.getItemType()).isEqualTo("PECA");
        assertThat(response.getUnitOfMeasure()).isEqualTo("UNIDADE");
        assertThat(response.getInventoryQuantity()).isEqualTo(10);
    }

    @Test
    void shouldMapInventoryMovementDomainToResponse() {
        // Given / When
        InventoryMovementResponse response = movementMapper.toResponse(entryMovement());

        // Then
        assertThat(response.getId()).isEqualTo(entryMovement().getId());
        assertThat(response.getMovementType()).isEqualTo(ENTRADA);
        assertThat(response.getQuantityBefore()).isEqualTo(10);
        assertThat(response.getQuantityAfter()).isEqualTo(15);
    }
}
