package br.com.fiap.numberone.inventory.domain.exceptions;

public class InventoryItemAlreadyInactiveException extends RuntimeException {
    public InventoryItemAlreadyInactiveException(String message) {
        super(message);
    }
}