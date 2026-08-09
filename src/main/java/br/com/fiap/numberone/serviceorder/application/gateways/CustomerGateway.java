package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.domain.references.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerGateway {
    Optional<Customer> findById(UUID id);
}
