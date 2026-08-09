package br.com.fiap.numberone.serviceorder.application.commands;

import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
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
public class ServiceOrderFinalDiagnosisUpdate {

    private UUID serviceOrderId;
    private String finalDiagnosisDescription;
    private String notes;
    private LocalDateTime expectedDateTime;
    private ServiceOrderStatus status;
}
