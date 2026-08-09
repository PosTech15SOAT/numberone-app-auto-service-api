package br.com.fiap.numberone.serviceorder.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.service-order.approval")
public record ServiceOrderApprovalProperties(
        String baseUrl
) {
}
