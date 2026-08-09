package br.com.fiap.numberone.inventory.domain.exceptions;

public class InvalidInventoryItemDataException extends RuntimeException {
    public InvalidInventoryItemDataException(String message) {
        super(message);
    }
}