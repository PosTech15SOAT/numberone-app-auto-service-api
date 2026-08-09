package br.com.fiap.numberone.serviceorder.infrastructure.notifications;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetApprovalNotificationGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.infrastructure.config.ServiceOrderApprovalProperties;
import br.com.fiap.numberone.shared.application.gateways.EmailGateway;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Component
public class EmailServiceOrderBudgetApprovalNotificationGateway implements ServiceOrderBudgetApprovalNotificationGateway {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EmailGateway emailGateway;
    private final ServiceOrderApprovalProperties properties;

    public EmailServiceOrderBudgetApprovalNotificationGateway(
            EmailGateway emailGateway,
            ServiceOrderApprovalProperties properties
    ) {
        this.emailGateway = emailGateway;
        this.properties = properties;
    }

    @Override
    public void sendApprovalRequest(ServiceOrderBudget serviceOrderBudget, String recipientEmail) {
        String approvalUrl = buildDecisionUrl(serviceOrderBudget.getId(), "aprovar");
        String rejectionUrl = buildDecisionUrl(serviceOrderBudget.getId(), "rejeitar");
        String expectedDeliveryDateTime = formatDateTime(serviceOrderBudget.getServiceOrder().getExpectedDateTime());
        String vehicleSummary = buildVehicleSummary(serviceOrderBudget);
        String servicesSummary = buildServicesSummary(serviceOrderBudget);

        String subject = "Aprovacao de orcamento da ordem de servico " + serviceOrderBudget.getServiceOrder().getId();
        String body = """
                Um orcamento de ordem de servico esta aguardando aprovacao.

                ID da ordem de servico: %s
                ID do orcamento: %s
                Veiculo: %s
                Valor orcado: %s
                Data prevista de entrega: %s

                Servicos previstos:
                %s

                Aprovar:
                %s

                Rejeitar:
                %s
                """.formatted(
                serviceOrderBudget.getServiceOrder().getId(),
                serviceOrderBudget.getId(),
                vehicleSummary,
                serviceOrderBudget.getQuotedAmount(),
                expectedDeliveryDateTime,
                servicesSummary,
                approvalUrl,
                rejectionUrl
        );

        emailGateway.send(recipientEmail, subject, body);
    }

    private String buildDecisionUrl(UUID budgetId, String decision) {
        return UriComponentsBuilder.fromUriString(properties.baseUrl())
                .path("/api/public/orcamentos-ordem-servico/{id}/aprovacao/{decision}")
                .buildAndExpand(budgetId, decision)
                .toUriString();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Nao informada";
        }

        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private String buildServicesSummary(ServiceOrderBudget serviceOrderBudget) {
        if (serviceOrderBudget.getServiceOrder() == null || serviceOrderBudget.getServiceOrder().getServiceItems() == null) {
            return "- Nenhum servico informado";
        }

        String servicesSummary = serviceOrderBudget.getServiceOrder().getServiceItems()
                .stream()
                .filter(Objects::nonNull)
                .filter(serviceOrderItem -> serviceOrderItem.getStatus() != OrderItemStatus.CANCELLED)
                .map(this::formatServiceItem)
                .reduce((first, second) -> first + System.lineSeparator() + second)
                .orElse("- Nenhum servico informado");

        return servicesSummary;
    }

    private String formatServiceItem(ServiceOrderItem serviceOrderItem) {
        String serviceName = "Servico nao identificado";
        if (serviceOrderItem.getAutomotiveService() != null && serviceOrderItem.getAutomotiveService().getName() != null) {
            serviceName = serviceOrderItem.getAutomotiveService().getName();
        }

        return "- " + serviceName;
    }

    private String buildVehicleSummary(ServiceOrderBudget serviceOrderBudget) {
        if (serviceOrderBudget.getServiceOrder() == null || serviceOrderBudget.getServiceOrder().getVehicle() == null) {
            return "Veiculo nao informado";
        }

        String brand = defaultText(serviceOrderBudget.getServiceOrder().getVehicle().getBrand());
        String model = defaultText(serviceOrderBudget.getServiceOrder().getVehicle().getModel());
        String licensePlate = defaultText(serviceOrderBudget.getServiceOrder().getVehicle().getLicensePlate());

        return "%s %s - placa %s".formatted(brand, model, licensePlate);
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "Nao informado" : value;
    }
}
