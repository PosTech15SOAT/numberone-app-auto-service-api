package br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways;

import br.com.fiap.numberone.customer.infrastructure.persistence.repositories.CustomerRepository;
import br.com.fiap.numberone.serviceorder.application.gateways.CustomerGateway;
import br.com.fiap.numberone.serviceorder.domain.references.Customer;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.CustomerMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerGatewayImpl implements CustomerGateway {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerGatewayImpl(CustomerRepository customerRepository, CustomerMapper mapper) {
        this.repository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}


