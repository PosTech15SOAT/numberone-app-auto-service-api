package br.com.fiap.numberone.shared.infrastructure.logging;

import br.com.fiap.numberone.shared.application.gateways.LoggerGateway;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Slf4jLoggerGateway implements LoggerGateway {

    @Override
    public void info(String message, Object... args) {
        log.info(formatMessage(message, args));
    }

    @Override
    public void infoWithContext(String message, Object... keyValues) {
        if (keyValues == null || keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Structured log context requires key/value pairs");
        }

        List<String> appliedKeys = new ArrayList<>();
        Map<String, String> previousValues = new HashMap<>();

        try {
            for (int index = 0; index < keyValues.length; index += 2) {
                Object key = keyValues[index];
                Object value = keyValues[index + 1];
                if (key == null || value == null) {
                    continue;
                }

                String mdcKey = key.toString();
                String previousValue = MDC.get(mdcKey);
                if (previousValue != null) {
                    previousValues.put(mdcKey, previousValue);
                }
                MDC.put(mdcKey, value.toString());
                appliedKeys.add(mdcKey);
            }

            log.info(message);
        } finally {
            for (int index = appliedKeys.size() - 1; index >= 0; index--) {
                String key = appliedKeys.get(index);
                String previousValue = previousValues.get(key);
                if (previousValue == null) {
                    MDC.remove(key);
                } else {
                    MDC.put(key, previousValue);
                }
            }
        }
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
