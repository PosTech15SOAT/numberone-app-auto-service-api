package br.com.fiap.numberone.shared.infrastructure.logging;

import br.com.fiap.numberone.shared.application.gateways.LoggerGateway;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;

@Slf4j
public class Slf4jLoggerGateway implements LoggerGateway {

    @Override
    public void info(String message, Object... args) {
        log.info(formatMessage(message, args));
    }

    @Override
    public void warn(String message, Object... args) {
        log.warn(formatMessage(message, args));
    }

    @Override
    public void error(String message, Object... args) {
        log.error(formatMessage(message, args));
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        log.error(formatMessage(message, args), throwable);
    }

    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }

        String formattedMessage = message;
        for (Object arg : args) {
            formattedMessage = formattedMessage.replaceFirst("\\{}", String.valueOf(arg));
        }

        Map<String, String> mdc = MDC.getCopyOfContextMap();
        if (mdc != null && !mdc.isEmpty()) {
            formattedMessage = String.format("%s, mdc=%s", formattedMessage, mdc);
        }

        return formattedMessage;
    }
}
