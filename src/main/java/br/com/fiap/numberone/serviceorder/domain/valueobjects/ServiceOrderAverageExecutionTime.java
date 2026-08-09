package br.com.fiap.numberone.serviceorder.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderAverageExecutionTime {

    private UUID serviceOrderId;
    private Integer completedServices;
    private Integer pendingServices;
    private Integer inProgressServices;
    private Integer cancelledServices;
    private Integer waitingServices;
    private Long averageExecutionMinutes;
}
