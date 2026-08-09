package br.com.fiap.numberone.inventory.unit.application;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.application.services.InventoryItemService;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemBusinessException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.inventoryItem;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.newInventoryItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryItemServiceTest {

    private InventoryItemGateway inventoryItemGateway;
    private InventoryItemService inventoryItemService;

    @BeforeEach
    void setUp() {
        inventoryItemGateway = mock(InventoryItemGateway.class);
        inventoryItemService = new InventoryItemService(inventoryItemGateway);
    }

    @Test
    void shouldCreateInventoryItemWhenCodeIsUnique() {
        // Given
        InventoryItem item = newInventoryItem("FILTRO-001");
        when(inventoryItemGateway.existsByCode("FILTRO-001")).thenReturn(false);
        when(inventoryItemGateway.save(item)).thenReturn(item);

        // When
        InventoryItem result = inventoryItemService.create(item);

        // Then
        assertThat(result).isSameAs(item);
        verify(inventoryItemGateway).existsByCode("FILTRO-001");
        verify(inventoryItemGateway).save(item);
    }

    @Test
    void shouldRejectCreateWhenCodeAlreadyExists() {
        // Given
        InventoryItem item = newInventoryItem("FILTRO-001");
        when(inventoryItemGateway.existsByCode("FILTRO-001")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> inventoryItemService.create(item))
                .isInstanceOf(InventoryItemBusinessException.class)
                .hasMessage("Já existe um item de estoque com o código informado");

        verify(inventoryItemGateway, never()).save(item);
    }

    @Test
    void shouldUpdateInventoryItem() {
        // Given
        UUID id = UUID.randomUUID();
        InventoryItem current = inventoryItem(id, "OLEO-001", true, 10);
        InventoryItem newData = newInventoryItem("FILTRO-001");
        when(inventoryItemGateway.findById(id)).thenReturn(Optional.of(current));
        when(inventoryItemGateway.findByCode("FILTRO-001")).thenReturn(Optional.empty());
        when(inventoryItemGateway.save(current)).thenReturn(current);

        // When
        InventoryItem result = inventoryItemService.update(id, newData);

        // Then
        assertThat(result.getCode()).isEqualTo("FILTRO-001");
        assertThat(result.getName()).isEqualTo("Filtro de oleo");
        verify(inventoryItemGateway).save(current);
    }

    @Test
    void shouldRejectUpdateWhenItemDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(inventoryItemGateway.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> inventoryItemService.update(id, newInventoryItem("FILTRO-001")))
                .isInstanceOf(InventoryItemNotFoundException.class)
                .hasMessage("Item de estoque não encontrado");
    }

    @Test
    void shouldRejectUpdateWhenCodeBelongsToAnotherItem() {
        // Given
        UUID id = UUID.randomUUID();
        InventoryItem current = inventoryItem(id, "OLEO-001", true, 10);
        InventoryItem another = inventoryItem(UUID.randomUUID(), "FILTRO-001", true, 5);
        when(inventoryItemGateway.findById(id)).thenReturn(Optional.of(current));
        when(inventoryItemGateway.findByCode("FILTRO-001")).thenReturn(Optional.of(another));

        // When / Then
        assertThatThrownBy(() -> inventoryItemService.update(id, newInventoryItem("FILTRO-001")))
                .isInstanceOf(InventoryItemBusinessException.class)
                .hasMessage("Já existe outro item de estoque com o código informado");

        verify(inventoryItemGateway, never()).save(current);
    }

    @Test
    void shouldFindAllActiveInventoryItems() {
        // Given
        List<InventoryItem> items = List.of(inventoryItem(), inventoryItem(UUID.randomUUID(), "FILTRO-001", true, 5));
        when(inventoryItemGateway.findAllActive()).thenReturn(items);

        // When
        List<InventoryItem> result = inventoryItemService.findAll();

        // Then
        assertThat(result).containsExactlyElementsOf(items);
    }

    @Test
    void shouldFindInventoryItemById() {
        // Given
        InventoryItem item = inventoryItem();
        when(inventoryItemGateway.findById(item.getId())).thenReturn(Optional.of(item));

        // When
        InventoryItem result = inventoryItemService.findById(item.getId());

        // Then
        assertThat(result).isSameAs(item);
    }

    @Test
    void shouldActivateAndInactivateInventoryItem() {
        // Given
        UUID activeItemId = UUID.randomUUID();
        UUID inactiveItemId = UUID.randomUUID();
        InventoryItem activeItem = inventoryItem(activeItemId, "OLEO-001", true, 10);
        InventoryItem inactiveItem = inventoryItem(inactiveItemId, "FILTRO-001", false, 10);
        when(inventoryItemGateway.findById(activeItemId)).thenReturn(Optional.of(activeItem));
        when(inventoryItemGateway.findById(inactiveItemId)).thenReturn(Optional.of(inactiveItem));

        // When
        inventoryItemService.inactivate(activeItemId);
        inventoryItemService.activate(inactiveItemId);

        // Then
        assertThat(activeItem.isActive()).isFalse();
        assertThat(inactiveItem.isActive()).isTrue();
        verify(inventoryItemGateway).save(activeItem);
        verify(inventoryItemGateway).save(inactiveItem);
    }
}
