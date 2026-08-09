package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemCompletionUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemStartUpdate;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderItemGateway {

    ServiceOrderItem save(ServiceOrderItem serviceOrderItem);

    Optional<ServiceOrderItem> findById(UUID id);

    List<ServiceOrderItem> findAll();

    void deleteById(UUID id);

    ServiceOrderItem updateStatus(UUID id, OrderItemStatus status);

    ServiceOrderItem start(ServiceOrderItemStartUpdate update);

    ServiceOrderItem complete(ServiceOrderItemCompletionUpdate update);
}
