package br.com.fiap.numberone.shared.infrastructure.correlation;

public class MissingCorrelationIdException extends RuntimeException {

	public MissingCorrelationIdException(String message) {
		super(message);
	}
}
