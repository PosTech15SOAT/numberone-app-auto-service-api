package br.com.fiap.numberone.serviceorder.application.gateways;

import br.com.fiap.numberone.serviceorder.domain.references.AutomotiveService;

import java.util.Optional;
import java.util.UUID;

public interface AutomotiveServiceGateway {
    Optional<AutomotiveService> findById(UUID id);
}
