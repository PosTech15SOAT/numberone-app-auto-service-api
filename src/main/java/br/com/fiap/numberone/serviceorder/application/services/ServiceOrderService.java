package br.com.fiap.numberone.serviceorder.application.services;

import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderDeliveryUpdate;
import br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderFinalDiagnosisUpdate;
import br.com.fiap.numberone.serviceorder.application.gateways.CustomerGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.VehicleGateway;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.Diagnosis;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;
import br.com.fiap.numberone.serviceorder.domain.references.Customer;
import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderAverageExecutionTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderEstimatedTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderValue;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServiceOrderService {

    private final ServiceOrderGateway serviceOrderGateway;
    private final CustomerGateway customerGateway;
    private final VehicleGateway vehicleGateway;

    public ServiceOrderService(
            ServiceOrderGateway serviceOrderGateway,
            CustomerGateway customerGateway,
            VehicleGateway vehicleGateway
    ) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.customerGateway = customerGateway;
        this.vehicleGateway = vehicleGateway;
    }

    public List<ServiceOrder> getServiceOrders() {
        return serviceOrderGateway.findAll();
    }

    public ServiceOrder createServiceOrder(ServiceOrder serviceOrder) {
        Customer validatedCustomer = customerGateway.findById(serviceOrder.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Vehicle validatedVehicle = vehicleGateway.findById(serviceOrder.getVehicle().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        serviceOrder.attachCustomer(validatedCustomer);
        serviceOrder.attachVehicle(validatedVehicle);

        return serviceOrderGateway.save(serviceOrder);
    }

    public ServiceOrder addFinalDiagnosis(UUID id, Diagnosis diagnosis) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        serviceOrder.applyFinalDiagnosis(diagnosis.getFinalDiagnosisDescription(), diagnosis.getNotes());
        serviceOrder.defineExpectedDateTime(diagnosis.getExpectedDateTime());
        serviceOrder.updateStatus(ServiceOrderStatus.IN_DIAGNOSIS);

        return serviceOrderGateway.updateFinalDiagnosis(
                ServiceOrderFinalDiagnosisUpdate.builder()
                        .serviceOrderId(serviceOrder.getId())
                        .finalDiagnosisDescription(serviceOrder.getFinalDiagnosisDescription())
                        .notes(serviceOrder.getNotes())
                        .expectedDateTime(serviceOrder.getExpectedDateTime())
                        .status(serviceOrder.getStatus())
                        .build()
        );
    }

    public ServiceOrder startOrderService(UUID id) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        return changeOrderStatus(serviceOrder, ServiceOrderStatus.IN_PROGRESS);
    }

    public ServiceOrder cancelOrderService(UUID id) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        return changeOrderStatus(serviceOrder, ServiceOrderStatus.CANCELLED);
    }

    public ServiceOrder completeOrderService(UUID id) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        serviceOrder.validateServiceItemsAreFinished();
        return changeOrderStatus(serviceOrder, ServiceOrderStatus.COMPLETED);
    }


    public ServiceOrder deliverOrderService(UUID id) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        if (serviceOrder.getStatus() == ServiceOrderStatus.COMPLETED) {
            serviceOrder.validateServiceItemsAreFinished();
        }

        serviceOrder.updateStatus(ServiceOrderStatus.DELIVERED);

        return serviceOrderGateway.deliver(
                ServiceOrderDeliveryUpdate.builder()
                        .serviceOrderId(serviceOrder.getId())
                        .deliveryDateTime(LocalDateTime.now())
                        .status(serviceOrder.getStatus())
                        .build()
        );
    }

    public ServiceOrderValue calculateServices(UUID id) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        BigDecimal totalValue = serviceOrder.getServiceItemsTotalValue();

        return ServiceOrderValue.builder()
                .serviceOrderId(id)
                .totalValue(totalValue)
                .build();
    }

    public ServiceOrderEstimatedTime calculateEstimatedTime(UUID id) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        int totalEstimatedMinutes = serviceOrder.getServiceItems()
                .stream()
                .filter(serviceOrderItem -> serviceOrderItem.getStatus() != OrderItemStatus.CANCELLED)
                .map(ServiceOrderItem::getAutomotiveService)
                .filter(Objects::nonNull)
                .map(AutomotiveService::getEstimatedTimeMinutes)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);

        return ServiceOrderEstimatedTime.builder()
                .serviceOrderId(id)
                .totalEstimatedMinutes(totalEstimatedMinutes)
                .suggestedExpectedDateTime(LocalDateTime.now().plusMinutes(totalEstimatedMinutes))
                .build();
    }

    public ServiceOrderAverageExecutionTime calculateAverageServiceExecutionTime(UUID id) {
        ServiceOrder serviceOrder = getServiceOrder(id);

        List<ServiceOrderItem> items = serviceOrder.getServiceItems();

        int completedServices = countServicesByStatus(items, OrderItemStatus.COMPLETED);
        int pendingServices = countServicesByStatus(items, OrderItemStatus.PENDING);
        int inProgressServices = countServicesByStatus(items, OrderItemStatus.IN_PROGRESS);
        int cancelledServices = countServicesByStatus(items, OrderItemStatus.CANCELLED);
        int waitingServices = countServicesByStatus(items, OrderItemStatus.WAITING_FOR_PARTS_AND_SUPPLIES);

        long averageExecutionMinutes = (long) items.stream()
                .filter(item -> item.getStatus() == OrderItemStatus.COMPLETED)
                .filter(item -> item.getStartDateTime() != null && item.getEndDateTime() != null)
                .mapToLong(item -> Duration.between(item.getStartDateTime(), item.getEndDateTime()).toMinutes())
                .average()
                .orElse(0);

        return ServiceOrderAverageExecutionTime.builder()
                .serviceOrderId(serviceOrder.getId())
                .completedServices(completedServices)
                .pendingServices(pendingServices)
                .inProgressServices(inProgressServices)
                .cancelledServices(cancelledServices)
                .waitingServices(waitingServices)
                .averageExecutionMinutes(averageExecutionMinutes)
                .build();
    }

    private static int countServicesByStatus(List<ServiceOrderItem> items, OrderItemStatus completed) {
        return (int) items.stream()
                .filter(item -> item.getStatus() == completed)
                .count();
    }


    public ServiceOrder getServiceOrder(UUID id) {
        return serviceOrderGateway.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found for id: " + id));
    }

    private ServiceOrder changeOrderStatus(ServiceOrder serviceOrder, ServiceOrderStatus targetStatus) {
        serviceOrder.updateStatus(targetStatus);
        return serviceOrderGateway.updateStatus(serviceOrder.getId(), serviceOrder.getStatus());
    }

}
