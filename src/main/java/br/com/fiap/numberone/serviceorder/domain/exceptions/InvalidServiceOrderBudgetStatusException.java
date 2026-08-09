package br.com.fiap.numberone.serviceorder.domain.exceptions;

public class InvalidServiceOrderBudgetStatusException extends RuntimeException {

    public InvalidServiceOrderBudgetStatusException(String message) {
        super(message);
    }
}
