package br.com.fiap.numberone.inventory.unit.domain;

import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.exceptions.InvalidInventoryItemDataException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemAlreadyActiveException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemAlreadyInactiveException;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryItemBusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.fiap.numberone.inventory.domain.enums.ItemType.PECA;
import static br.com.fiap.numberone.inventory.domain.enums.UnitOfMeasure.UNIDADE;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.inventoryItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryItemTest {

    @Test
    void shouldCreateInventoryItemWithDefaultActiveStatusWhenActiveIsNull() {
        // Given / When
        InventoryItem item = InventoryItem.create(
                "OLEO-001",
                "Oleo de motor",
                "Oleo sintetico 5W30",
                PECA,
                UNIDADE,
                new BigDecimal("45.90"),
                new BigDecimal("79.90"),
                10,
                3,
                "MotorOil",
                "Universal",
                null
        );

        // Then
        assertThat(item.getId()).isNotNull();
        assertThat(item.getCode()).isEqualTo("OLEO-001");
        assertThat(item.isActive()).isTrue();
        assertThat(item.getCreatedAt()).isNotNull();
        assertThat(item.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectInventoryItemWithInvalidRequiredData() {
        // Given / When / Then
        assertThatThrownBy(() -> InventoryItem.create(
                "",
                "Oleo de motor",
                "Oleo sintetico 5W30",
                PECA,
                UNIDADE,
                new BigDecimal("45.90"),
                new BigDecimal("79.90"),
                10,
                3,
                "MotorOil",
                "Universal",
                true
        ))
                .isInstanceOf(InvalidInventoryItemDataException.class)
                .hasMessage("O código do item é obrigatório");
    }

    @Test
    void shouldUpdateInventoryItemData() {
        // Given
        InventoryItem item = inventoryItem();

        // When
        item.update(
                "FILTRO-001",
                "Filtro de oleo",
                "Filtro blindado",
                PECA,
                UNIDADE,
                new BigDecimal("28.00"),
                new BigDecimal("52.00"),
                8,
                2,
                "FilterOne",
                "Universal"
        );

        // Then
        assertThat(item.getCode()).isEqualTo("FILTRO-001");
        assertThat(item.getName()).isEqualTo("Filtro de oleo");
        assertThat(item.getInventoryQuantity()).isEqualTo(8);
        assertThat(item.getMinimumInventoryQuantity()).isEqualTo(2);
    }

    @Test
    void shouldControlInventoryQuantity() {
        // Given
        InventoryItem item = inventoryItem();

        // When
        item.addInventoryQuantity(5);
        item.removeInventoryQuantity(4);
        item.adjustInventoryQuantity(2);

        // Then
        assertThat(item.getInventoryQuantity()).isEqualTo(2);
        assertThat(item.isBelowMinimumInventory()).isTrue();
    }

    @Test
    void shouldRejectInvalidInventoryMovements() {
        // Given
        InventoryItem item = inventoryItem();

        // When / Then
        assertThatThrownBy(() -> item.addInventoryQuantity(0))
                .isInstanceOf(InventoryItemBusinessException.class)
                .hasMessage("Quantidade de entrada deve ser maior que zero");

        assertThatThrownBy(() -> item.removeInventoryQuantity(11))
                .isInstanceOf(InventoryItemBusinessException.class)
                .hasMessage("Baixa não pode deixar o estoque negativo");

        assertThatThrownBy(() -> item.adjustInventoryQuantity(-1))
                .isInstanceOf(InventoryItemBusinessException.class)
                .hasMessage("Quantidade final do ajuste deve ser maior ou igual a zero");
    }

    @Test
    void shouldActivateAndDeactivateInventoryItem() {
        // Given
        InventoryItem activeItem = inventoryItem();
        InventoryItem inactiveItem = inventoryItem(activeItem.getId(), "OLEO-002", false, 10);

        // When
        activeItem.deactivate();
        inactiveItem.activate();

        // Then
        assertThat(activeItem.isActive()).isFalse();
        assertThat(inactiveItem.isActive()).isTrue();
    }

    @Test
    void shouldRejectDuplicatedActivationStateChanges() {
        // Given
        InventoryItem activeItem = inventoryItem();
        InventoryItem inactiveItem = inventoryItem(activeItem.getId(), "OLEO-002", false, 10);

        // When / Then
        assertThatThrownBy(activeItem::activate)
                .isInstanceOf(InventoryItemAlreadyActiveException.class)
                .hasMessage("O item de estoque já está ativo");

        assertThatThrownBy(inactiveItem::deactivate)
                .isInstanceOf(InventoryItemAlreadyInactiveException.class)
                .hasMessage("O item de estoque já está inativo");
    }
}
