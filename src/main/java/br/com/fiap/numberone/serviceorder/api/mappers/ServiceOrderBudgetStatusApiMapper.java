package br.com.fiap.numberone.serviceorder.api.mappers;

import br.com.fiap.numberone.serviceorder.api.dtos.responses.ServiceOrderBudgetStatusResponse;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderBudgetStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ServiceOrderBudgetStatusApiMapper {

    @ValueMappings({
            @ValueMapping(source = "DRAFT", target = "RASCUNHO"),
            @ValueMapping(source = "SENT", target = "ENVIADO"),
            @ValueMapping(source = "APPROVED", target = "APROVADO"),
            @ValueMapping(source = "REJECTED", target = "REJEITADO"),
            @ValueMapping(source = "CANCELLED", target = "CANCELADO")
    })
    ServiceOrderBudgetStatusResponse toResponse(ServiceOrderBudgetStatus status);
}
