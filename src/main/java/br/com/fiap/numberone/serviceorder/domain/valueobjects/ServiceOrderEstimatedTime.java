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
public class ServiceOrderEstimatedTime {

    private UUID serviceOrderId;
    private Integer totalEstimatedMinutes;
    private LocalDateTime suggestedExpectedDateTime;
}
