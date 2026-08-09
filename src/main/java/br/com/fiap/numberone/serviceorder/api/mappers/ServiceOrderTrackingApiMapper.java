package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderTrackingResponse;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderBudget;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrderItem;
import br.com.fiap.numberone.serviceorder.domain.references.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = {
        ServiceOrderStatusApiMapper.class,
        ServiceOrderItemStatusApiMapper.class,
        ServiceOrderBudgetStatusApiMapper.class
})
public interface ServiceOrderTrackingApiMapper {

    @Mapping(target = "budget", expression = "java(getLatestBudgetResponse(entity.getBudgets()))")
    ServiceOrderTrackingResponse toResponse(ServiceOrder entity);

    @Mapping(target = "licensePlate", source = "licensePlate")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "model", source = "model")
    @Mapping(target = "year", source = "year")
    ServiceOrderTrackingResponse.VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "serviceName", source = "automotiveService.name")
    @Mapping(target = "serviceType", source = "automotiveService.serviceType")
    ServiceOrderTrackingResponse.ServiceItemResponse toResponse(ServiceOrderItem serviceOrderItem);

    ServiceOrderTrackingResponse.BudgetResponse toResponse(ServiceOrderBudget serviceOrderBudget);

    default ServiceOrderTrackingResponse.BudgetResponse getLatestBudgetResponse(List<ServiceOrderBudget> budgets) {
        if (budgets == null || budgets.isEmpty()) {
            return null;
        }

        ServiceOrderBudget latestBudget = budgets.stream()
                .filter(java.util.Objects::nonNull)
                .max(Comparator.comparing(ServiceOrderBudget::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        return latestBudget == null ? null : toResponse(latestBudget);
    }
}
