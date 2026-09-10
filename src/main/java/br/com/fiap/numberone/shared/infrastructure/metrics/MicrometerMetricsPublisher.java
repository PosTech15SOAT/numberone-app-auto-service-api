package br.com.fiap.numberone.shared.infrastructure.metrics;

import br.com.fiap.numberone.shared.application.gateways.MetricsGateway;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class MicrometerMetricsPublisher implements MetricsGateway {

    private final MeterRegistry meterRegistry;

    public MicrometerMetricsPublisher(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void incrementCounter(
            String metricName,
            String... tags
    ) {
        meterRegistry
                .counter(metricName, Tags.of(tags))
                .increment();
    }

    @Override
    public void recordTimer(
            String metricName,
            Duration duration,
            String... tags
    ) {
        meterRegistry
                .timer(metricName, Tags.of(tags))
                .record(duration);
    }
}