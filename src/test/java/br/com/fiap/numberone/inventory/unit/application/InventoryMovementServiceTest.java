package br.com.fiap.numberone.inventory.unit.application;

import br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.inventory.application.gateways.InventoryMovementGateway;
import br.com.fiap.numberone.inventory.application.services.InventoryMovementService;
import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementType;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryBusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.MANUAL;
import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.COMPRA;
import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.ORDEM_SERVICO;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ITEM_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ORIGIN_REFERENCE_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.RESPONSIBLE_USER_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.entryMovement;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.inventoryItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryMovementServiceTest {

    private InventoryItemGateway itemGateway;
    private InventoryMovementGateway movementGateway;
    private InventoryMovementService inventoryMovementService;

    @BeforeEach
    void setUp() {
        itemGateway = mock(InventoryItemGateway.class);
        movementGateway = mock(InventoryMovementGateway.class);
        inventoryMovementService = new InventoryMovementService(itemGateway, movementGateway);
    }

    @Test
    void shouldRegisterEntry() {
        // Given
        InventoryItem item = inventoryItem(ITEM_ID, "OLEO-001", true, 10);
        when(itemGateway.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(movementGateway.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InventoryMovement result = inventoryMovementService.registerEntry(
                ITEM_ID, 5, COMPRA, ORIGIN_REFERENCE_ID, "Compra de reposicao", RESPONSIBLE_USER_ID
        );

        // Then
        assertThat(result.getMovementType()).isEqualTo(InventoryMovementType.ENTRADA);
        assertThat(result.getQuantityBefore()).isEqualTo(10);
        assertThat(result.getQuantityAfter()).isEqualTo(15);
        assertThat(item.getInventoryQuantity()).isEqualTo(15);
        verify(itemGateway).save(item);
    }

    @Test
    void shouldRegisterWithdrawal() {
        // Given
        InventoryItem item = inventoryItem(ITEM_ID, "OLEO-001", true, 10);
        when(itemGateway.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(movementGateway.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InventoryMovement result = inventoryMovementService.registerWithdrawal(
                ITEM_ID, 4, ORDEM_SERVICO, ORIGIN_REFERENCE_ID, "Uso em ordem de servico", RESPONSIBLE_USER_ID
        );

        // Then
        assertThat(result.getMovementType()).isEqualTo(InventoryMovementType.BAIXA);
        assertThat(result.getQuantityBefore()).isEqualTo(10);
        assertThat(result.getQuantityAfter()).isEqualTo(6);
        assertThat(item.getInventoryQuantity()).isEqualTo(6);
        verify(itemGateway).save(item);
    }

    @Test
    void shouldRegisterAdjustment() {
        // Given
        InventoryItem item = inventoryItem(ITEM_ID, "OLEO-001", true, 10);
        when(itemGateway.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(movementGateway.save(any(InventoryMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InventoryMovement result = inventoryMovementService.registerAdjustment(
                ITEM_ID, 7, MANUAL, ORIGIN_REFERENCE_ID, "Contagem manual", RESPONSIBLE_USER_ID
        );

        // Then
        assertThat(result.getMovementType()).isEqualTo(InventoryMovementType.AJUSTE);
        assertThat(result.getQuantityBefore()).isEqualTo(10);
        assertThat(result.getQuantityAfter()).isEqualTo(7);
        assertThat(item.getInventoryQuantity()).isEqualTo(7);
        verify(itemGateway).save(item);
    }

    @Test
    void shouldRejectMovementWhenItemDoesNotExist() {
        // Given
        when(itemGateway.findById(ITEM_ID)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> inventoryMovementService.registerEntry(
                ITEM_ID, 5, COMPRA, ORIGIN_REFERENCE_ID, "Compra", RESPONSIBLE_USER_ID
        ))
                .isInstanceOf(InventoryBusinessException.class)
                .hasMessage("Item de estoque não encontrado");

        verify(itemGateway, never()).save(any());
        verify(movementGateway, never()).save(any());
    }

    @Test
    void shouldRejectMovementWhenItemIsInactive() {
        // Given
        InventoryItem item = inventoryItem(ITEM_ID, "OLEO-001", false, 10);
        when(itemGateway.findById(ITEM_ID)).thenReturn(Optional.of(item));

        // When / Then
        assertThatThrownBy(() -> inventoryMovementService.registerEntry(
                ITEM_ID, 5, COMPRA, ORIGIN_REFERENCE_ID, "Compra", RESPONSIBLE_USER_ID
        ))
                .isInstanceOf(InventoryBusinessException.class)
                .hasMessage("Item de estoque inativo não pode receber movimentação");

        verify(itemGateway, never()).save(any());
        verify(movementGateway, never()).save(any());
    }

    @Test
    void shouldFindMovementsByInventoryItemId() {
        // Given
        UUID itemId = UUID.randomUUID();
        List<InventoryMovement> movements = List.of(entryMovement());
        when(movementGateway.findByInventoryItemId(itemId)).thenReturn(movements);

        // When
        List<InventoryMovement> result = inventoryMovementService.findByInventoryItemId(itemId);

        // Then
        assertThat(result).containsExactlyElementsOf(movements);
    }
}
