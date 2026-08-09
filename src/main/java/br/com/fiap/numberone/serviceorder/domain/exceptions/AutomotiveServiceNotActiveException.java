package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class AutomotiveServiceNotActiveException extends RuntimeException {
    public AutomotiveServiceNotActiveException(String message) {
        super(message);
    }
}
