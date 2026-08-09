package br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories;

import br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities.ServiceOrderEntity;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrderEntity, UUID> {

    @Modifying
    @Query("""
        update ServiceOrderEntity so
           set so.status = :status,
               so.updatedAt = CURRENT_TIMESTAMP
         where so.id = :id
    """)
    int updateStatus(@Param("id") UUID id, @Param("status") ServiceOrderStatus status);

    @Modifying
    @Query("""
    update ServiceOrderEntity so
       set so.finalDiagnosisDescription = :#{#update.finalDiagnosisDescription},
           so.notes = :#{#update.notes},
           so.expectedDateTime = :#{#update.expectedDateTime},
           so.status = :#{#update.status},
           so.updatedAt = CURRENT_TIMESTAMP
     where so.id = :#{#update.serviceOrderId}
    """)
    void updateFinalDiagnosis(@Param("update") br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderFinalDiagnosisUpdate update);

    @Modifying
    @Query("""
    update ServiceOrderEntity so
       set so.deliveryDateTime = :#{#update.deliveryDateTime},
           so.status = :#{#update.status},
           so.updatedAt = CURRENT_TIMESTAMP
     where so.id = :#{#update.serviceOrderId}
    """)
    void deliver(@Param("update") br.com.fiap.numberone.serviceorder.application.commands.ServiceOrderDeliveryUpdate update);
}
