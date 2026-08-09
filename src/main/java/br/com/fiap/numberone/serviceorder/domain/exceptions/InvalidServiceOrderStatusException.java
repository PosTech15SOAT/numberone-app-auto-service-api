package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class InvalidServiceOrderStatusException extends RuntimeException {
    public InvalidServiceOrderStatusException(String message) {
        super(message);
    }
}
