package br.com.fiap.numberone.shared.application.gateways;

import java.time.Duration;

public interface MetricsGateway {

    void incrementCounter(String metricName, String... tags);

    void recordTimer(
            String metricName,
            Duration duration,
            String... tags
    );
}