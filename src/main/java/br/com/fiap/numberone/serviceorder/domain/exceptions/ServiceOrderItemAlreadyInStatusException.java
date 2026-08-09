package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class ServiceOrderItemAlreadyInStatusException extends RuntimeException {

    public ServiceOrderItemAlreadyInStatusException(String message) {
        super(message);
    }
}
