package br.com.fiap.numberone.shared.config;

import br.com.fiap.numberone.shared.application.gateways.LoggerGateway;
import br.com.fiap.numberone.shared.infrastructure.logging.Slf4jLoggerGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerBeanConfig {


    @Bean
    public LoggerGateway loggerGateway() {
        return new Slf4jLoggerGateway();
    }
}
