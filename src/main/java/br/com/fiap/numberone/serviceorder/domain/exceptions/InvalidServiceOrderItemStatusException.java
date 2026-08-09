package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class InvalidServiceOrderItemStatusException extends RuntimeException {

    public InvalidServiceOrderItemStatusException(String message) {
        super(message);
    }
}
