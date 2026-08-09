package br.com.fiap.numberone.inventory.domain.exceptions;

public class InventoryItemAlreadyActiveException extends RuntimeException {
    public InventoryItemAlreadyActiveException(String message) {
        super(message);
    }
}