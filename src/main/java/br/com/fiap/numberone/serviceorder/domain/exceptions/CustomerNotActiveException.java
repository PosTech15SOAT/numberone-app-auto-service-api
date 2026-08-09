package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class CustomerNotActiveException extends RuntimeException {
    public CustomerNotActiveException(String message) {
        super(message);
    }
}
