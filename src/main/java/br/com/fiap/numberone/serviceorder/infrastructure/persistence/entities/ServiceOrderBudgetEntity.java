package br.com.fiap.numberone.serviceorder.infrastructure.persistence.entities;

import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderBudgetStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico_orcamento")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderBudgetEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordem_servico", nullable = false)
    private ServiceOrderEntity serviceOrder;

    @Column(name = "valor_proposto")
    private BigDecimal quotedAmount;

    @Column(name = "valor_aprovado")
    private BigDecimal approvedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceOrderBudgetStatus status;

    @Column(name = "enviado_em")
    private LocalDateTime sentAt;

    @Column(name = "aprovado_em")
    private LocalDateTime approvedAt;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ServiceOrderBudgetStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
