package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class CustomerEmailException extends RuntimeException {
    public CustomerEmailException(String message) {
        super(message);
    }
}
