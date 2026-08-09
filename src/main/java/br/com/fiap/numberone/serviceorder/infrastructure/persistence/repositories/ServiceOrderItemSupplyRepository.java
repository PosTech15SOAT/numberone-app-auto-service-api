package br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories;

import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemSupplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceOrderItemSupplyRepository extends JpaRepository<ServiceOrderItemSupplyEntity, UUID> {

    List<ServiceOrderItemSupplyEntity> findByServiceOrderItemId(UUID serviceOrderItemId);
}
