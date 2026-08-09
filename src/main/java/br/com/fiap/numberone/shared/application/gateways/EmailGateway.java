package br.com.fiap.numberone.shared.application.gateways;

public interface EmailGateway {

    void send(String recipientEmail, String subject, String body);
}
