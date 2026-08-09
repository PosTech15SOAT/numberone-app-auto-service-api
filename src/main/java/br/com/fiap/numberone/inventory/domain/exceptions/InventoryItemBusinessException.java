package br.com.fiap.numberone.inventory.domain.exceptions;

public class InventoryItemBusinessException extends RuntimeException {
    public InventoryItemBusinessException(String message) {
        super(message);
    }
}