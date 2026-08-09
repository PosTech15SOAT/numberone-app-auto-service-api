package br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers;

import br.com.fiap.numberone.customer.infrastructure.persistence.entities.CustomerEntity;
import br.com.fiap.numberone.serviceorder.domain.references.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "documentType", source = "documentType")
    @Mapping(target = "document", source = "document")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", source = "email")
    CustomerEntity toEntity(Customer customer);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "documentType", source = "documentType")
    @Mapping(target = "document", source = "document")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "email", source = "email")
    Customer toDomain(CustomerEntity entity);
}


