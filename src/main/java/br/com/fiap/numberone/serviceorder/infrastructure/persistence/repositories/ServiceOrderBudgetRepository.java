package br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories;

import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderBudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceOrderBudgetRepository extends JpaRepository<ServiceOrderBudgetEntity, UUID> {
}
