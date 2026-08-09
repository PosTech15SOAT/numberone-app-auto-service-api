package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderItemStatusResponse;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ServiceOrderItemStatusApiMapper {

    @ValueMappings({
            @ValueMapping(source = "PENDING", target = "PENDENTE"),
            @ValueMapping(source = "WAITING_FOR_PARTS_AND_SUPPLIES", target = "AGUARDANDO_PECAS_E_INSUMOS"),
            @ValueMapping(source = "IN_PROGRESS", target = "EM_EXECUCAO"),
            @ValueMapping(source = "CANCELLED", target = "CANCELADO"),
            @ValueMapping(source = "COMPLETED", target = "FINALIZADO")
    })
    ServiceOrderItemStatusResponse toResponse(OrderItemStatus status);
}
