package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class ServiceOrderItemSupplyUnavailableException extends RuntimeException {

    public ServiceOrderItemSupplyUnavailableException(String message) {
        super(message);
    }
}
