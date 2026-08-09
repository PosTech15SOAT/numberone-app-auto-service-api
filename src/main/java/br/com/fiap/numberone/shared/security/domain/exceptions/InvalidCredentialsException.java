package br.com.fiap.numberone.shared.security.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("Usuario ou senha invalidos.");
	}
}
