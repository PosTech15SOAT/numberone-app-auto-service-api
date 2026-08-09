package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.requests.CreateServiceOrderRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.requests.FinalDiagnosisRequest;
import br.com.fiap.numberone.serviceorder.api.dtos.responses.*;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItemSupply;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.Diagnosis;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.references.Customer;
import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderAverageExecutionTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderEstimatedTime;
import br.com.fiap.numberone.serviceorder.domain.valueobjects.ServiceOrderValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ServiceOrderStatusApiMapper.class,
        ServiceOrderItemStatusApiMapper.class,
        ServiceOrderBudgetStatusApiMapper.class
})
public interface ServiceOrderApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "expectedDateTime", ignore = true)
    @Mapping(target = "deliveryDateTime", ignore = true)
    @Mapping(target = "finalDiagnosisDescription", ignore = true)
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "vehicle.id", source = "vehicleId")
    ServiceOrder toDomain(CreateServiceOrderRequest dto);

    Diagnosis toDomain(FinalDiagnosisRequest dto);

    ServiceOrderValueResponse toResponse(ServiceOrderValue valueObject);

    ServiceOrderEstimatedTimeResponse toResponse(ServiceOrderEstimatedTime valueObject);

    ServiceOrderAverageExecutionTimeResponse toResponse(ServiceOrderAverageExecutionTime valueObject);

    ServiceOrderResponse toResponse(ServiceOrder entity);

    ServiceOrderResponse.CustomerResponse toResponse(Customer customer);

    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "model", source = "model")
    @Mapping(target = "year", source = "year")
    @Mapping(target = "customerId", source = "customerId")
    ServiceOrderResponse.VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "serviceOrderId", source = "serviceOrder.id")
    ServiceOrderItemResponse toResponse(ServiceOrderItem serviceOrderItem);

    @Mapping(target = "serviceOrderItemId", source = "serviceOrderItem.id")
    ServiceOrderItemSupplyResponse toResponse(ServiceOrderItemSupply serviceOrderItemSupply);

    @Mapping(target = "serviceOrderId", source = "serviceOrder.id")
    ServiceOrderBudgetResponse toResponse(ServiceOrderBudget serviceOrderBudget);
}
