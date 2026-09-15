package br.com.fiap.numberone.shared.infrastructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class Slf4jLoggerGatewayTest {

    private final Slf4jLoggerGateway loggerGateway = new Slf4jLoggerGateway();

    @AfterEach
    void cleanMdc() {
        MDC.clear();
    }

    @Test
    void shouldScopeBusinessContextAndPreserveRequestAndTraceContext() {
        Logger logger = (Logger) LoggerFactory.getLogger(Slf4jLoggerGateway.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        MDC.put("correlation_id", "request-123");
        MDC.put("dd.trace_id", "trace-456");
        MDC.put("dd.span_id", "span-789");

        try {
            loggerGateway.infoWithContext(
                    "Ordem de serviço criada",
                    "service_order_id", "order-1",
                    "status", "RECEIVED"
            );
        } finally {
            logger.detachAppender(appender);
        }

        ILoggingEvent event = appender.list.stream()
                .filter(loggingEvent -> loggingEvent.getLevel() == Level.INFO)
                .findFirst()
                .orElseThrow();

        assertThat(event.getFormattedMessage()).isEqualTo("Ordem de serviço criada");
        assertThat(event.getMDCPropertyMap())
                .containsEntry("correlation_id", "request-123")
                .containsEntry("service_order_id", "order-1")
                .containsEntry("status", "RECEIVED")
                .containsEntry("dd.trace_id", "trace-456")
                .containsEntry("dd.span_id", "span-789");

        assertThat(MDC.get("service_order_id")).isNull();
        assertThat(MDC.get("status")).isNull();
        assertThat(MDC.get("correlation_id")).isEqualTo("request-123");
        assertThat(MDC.get("dd.trace_id")).isEqualTo("trace-456");
        assertThat(MDC.get("dd.span_id")).isEqualTo("span-789");
    }
}
