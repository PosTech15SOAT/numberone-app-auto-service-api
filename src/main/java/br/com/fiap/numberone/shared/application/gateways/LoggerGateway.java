package br.com.fiap.numberone.shared.application.gateways;

public interface LoggerGateway {
    void info(String message, Object... args);
    void warn(String message, Object... args);
    void error(String message, Object... args);
    void error(String message, Throwable throwable, Object... args);
}
