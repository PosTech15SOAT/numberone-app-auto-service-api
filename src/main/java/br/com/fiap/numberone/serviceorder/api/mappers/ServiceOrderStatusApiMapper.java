package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderStatusResponse;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ServiceOrderStatusApiMapper {

    @ValueMappings({
            @ValueMapping(source = "RECEIVED", target = "RECEBIDA"),
            @ValueMapping(source = "IN_DIAGNOSIS", target = "EM_DIAGNOSTICO"),
            @ValueMapping(source = "WAITING_APPROVAL", target = "AGUARDANDO_APROVACAO"),
            @ValueMapping(source = "APPROVED", target = "APROVADA"),
            @ValueMapping(source = "REJECTED", target = "REJEITADA"),
            @ValueMapping(source = "IN_PROGRESS", target = "EM_EXECUCAO"),
            @ValueMapping(source = "COMPLETED", target = "FINALIZADA"),
            @ValueMapping(source = "CANCELLED", target = "CANCELADA"),
            @ValueMapping(source = "DELIVERED", target = "ENTREGUE")
    })
    ServiceOrderStatusResponse toResponse(ServiceOrderStatus status);
}
