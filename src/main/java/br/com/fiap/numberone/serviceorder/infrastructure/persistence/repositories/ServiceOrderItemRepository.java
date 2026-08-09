package br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories;

import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderItemEntity;
import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderItemRepository extends JpaRepository<ServiceOrderItemEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"automotiveService", "serviceOrder", "supplies", "supplies.inventoryItemEntity"})
    Optional<ServiceOrderItemEntity> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"automotiveService", "serviceOrder", "supplies", "supplies.inventoryItemEntity"})
    List<ServiceOrderItemEntity> findAll();

    @Modifying
    @Query("""
        update ServiceOrderItemEntity soi
           set soi.status = :status,
               soi.updatedAt = CURRENT_TIMESTAMP
         where soi.id = :id
    """)
    int updateStatus(@Param("id") UUID id, @Param("status") OrderItemStatus status);

    @Modifying
    @Query("""
        update ServiceOrderItemEntity soi
           set soi.startDateTime = :#{#update.startDateTime},
               soi.status = :#{#update.status},
               soi.updatedAt = CURRENT_TIMESTAMP
         where soi.id = :#{#update.serviceOrderItemId}
    """)
    int start(@Param("update") br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemStartUpdate update);

    @Modifying
    @Query("""
        update ServiceOrderItemEntity soi
           set soi.endDateTime = :#{#update.endDateTime},
               soi.status = :#{#update.status},
               soi.updatedAt = CURRENT_TIMESTAMP
         where soi.id = :#{#update.serviceOrderItemId}
    """)
    int complete(@Param("update") br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderItemCompletionUpdate update);
}
