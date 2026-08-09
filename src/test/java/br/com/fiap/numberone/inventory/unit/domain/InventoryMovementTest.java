package br.com.fiap.numberone.inventory.unit.domain;

import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.domain.enums.InventoryMovementType;
import br.com.fiap.numberone.inventory.domain.exceptions.InventoryBusinessException;
import org.junit.jupiter.api.Test;

import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.MANUAL;
import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.COMPRA;
import static br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.ORDEM_SERVICO;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ITEM_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ORIGIN_REFERENCE_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.RESPONSIBLE_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryMovementTest {

    @Test
    void shouldCreateEntryMovement() {
        // Given / When
        InventoryMovement movement = InventoryMovement.createEntry(
                ITEM_ID,
                COMPRA,
                ORIGIN_REFERENCE_ID,
                10,
                5,
                "Compra de reposicao",
                RESPONSIBLE_USER_ID
        );

        // Then
        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getMovementType()).isEqualTo(InventoryMovementType.ENTRADA);
        assertThat(movement.getQuantityBefore()).isEqualTo(10);
        assertThat(movement.getQuantityAfter()).isEqualTo(15);
    }

    @Test
    void shouldCreateWithdrawalMovement() {
        // Given / When
        InventoryMovement movement = InventoryMovement.createWithdrawal(
                ITEM_ID,
                ORDEM_SERVICO,
                ORIGIN_REFERENCE_ID,
                10,
                4,
                "Uso em ordem de servico",
                RESPONSIBLE_USER_ID
        );

        // Then
        assertThat(movement.getMovementType()).isEqualTo(InventoryMovementType.BAIXA);
        assertThat(movement.getQuantityBefore()).isEqualTo(10);
        assertThat(movement.getQuantityAfter()).isEqualTo(6);
    }

    @Test
    void shouldCreateAdjustmentMovement() {
        // Given / When
        InventoryMovement movement = InventoryMovement.createAdjustment(
                ITEM_ID,
                MANUAL,
                ORIGIN_REFERENCE_ID,
                10,
                7,
                "Contagem manual",
                RESPONSIBLE_USER_ID
        );

        // Then
        assertThat(movement.getMovementType()).isEqualTo(InventoryMovementType.AJUSTE);
        assertThat(movement.getQuantityBefore()).isEqualTo(10);
        assertThat(movement.getQuantityAfter()).isEqualTo(7);
    }

    @Test
    void shouldRejectInvalidMovementQuantities() {
        // Given / When / Then
        assertThatThrownBy(() -> InventoryMovement.createEntry(
                ITEM_ID, COMPRA, ORIGIN_REFERENCE_ID, 10, 0, "Compra", RESPONSIBLE_USER_ID
        ))
                .isInstanceOf(InventoryBusinessException.class)
                .hasMessage("Quantidade de entrada deve ser maior que zero");

        assertThatThrownBy(() -> InventoryMovement.createWithdrawal(
                ITEM_ID, ORDEM_SERVICO, ORIGIN_REFERENCE_ID, 3, 4, "Uso", RESPONSIBLE_USER_ID
        ))
                .isInstanceOf(InventoryBusinessException.class)
                .hasMessage("Baixa não pode deixar o estoque negativo");

        assertThatThrownBy(() -> InventoryMovement.createAdjustment(
                ITEM_ID, MANUAL, ORIGIN_REFERENCE_ID, 10, -1, "Contagem", RESPONSIBLE_USER_ID
        ))
                .isInstanceOf(InventoryBusinessException.class)
                .hasMessage("Quantidade final do ajuste deve ser maior ou igual a zero");
    }

    @Test
    void shouldRequireObservationForAdjustment() {
        // Given / When / Then
        assertThatThrownBy(() -> InventoryMovement.createAdjustment(
                ITEM_ID,
                MANUAL,
                ORIGIN_REFERENCE_ID,
                10,
                7,
                "",
                RESPONSIBLE_USER_ID
        ))
                .isInstanceOf(InventoryBusinessException.class)
                .hasMessage("Observação é obrigatória para ajuste");
    }
}
