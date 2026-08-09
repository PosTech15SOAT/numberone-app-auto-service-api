package br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities;

import br.com.fiap.numberone.customer.infrastructure.persistence.entities.CustomerEntity;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.vehicle.infrastructure.persistence.entities.VehicleEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "descricao_inicial")
    private String initialDescription;

    @Column(name = "descricao_diagnostico")
    private String diagnosisDescription;

    @Column(name = "descricao_diagnostico_final")
    private String finalDiagnosisDescription;

    @Column(name = "observacao")
    private String notes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private CustomerEntity customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veiculo")
    private VehicleEntity vehicleEntity;

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServiceOrderItemEntity> items = new ArrayList<>();

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServiceOrderBudgetEntity> budgets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceOrderStatus status = ServiceOrderStatus.RECEIVED;

    @Column(name = "data_hora_entrada")
    private LocalDateTime entryDateTime;

    @Column(name = "data_hora_prevista")
    private LocalDateTime expectedDateTime;

    @Column(name = "data_hora_entrega")
    private LocalDateTime deliveryDateTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ServiceOrderStatus.RECEIVED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void linkChildren() {
        if (items != null) {
            items.forEach(item -> {
                item.setServiceOrder(this);
                item.linkChildren();
            });
        }

        if (budgets != null) {
            budgets.forEach(budget -> budget.setServiceOrder(this));
        }
    }
}


