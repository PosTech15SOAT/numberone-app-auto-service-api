package br.com.fiap.numberone.inventory.domain.exceptions;

public class InventoryBusinessException extends RuntimeException {
    public InventoryBusinessException(String message) {
        super(message);
    }
}