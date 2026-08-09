package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderDeliveryUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderFinalDiagnosisUpdate;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderGateway {

    ServiceOrder save(ServiceOrder serviceOrder);

    Optional<ServiceOrder> findById(UUID id);

    List<ServiceOrder> findAll();

    ServiceOrder updateStatus(UUID id, ServiceOrderStatus status);

    ServiceOrder updateFinalDiagnosis(ServiceOrderFinalDiagnosisUpdate update);

    ServiceOrder deliver(ServiceOrderDeliveryUpdate update);
}
