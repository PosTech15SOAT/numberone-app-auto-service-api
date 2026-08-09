package br.com.fiap.numberone.serviceorder.application.commands;

import br.com.fiap.numberone.serviceorder.domain.enums.OrderItemStatus;
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
public class ServiceOrderItemStartUpdate {

    private UUID serviceOrderItemId;
    private LocalDateTime startDateTime;
    private OrderItemStatus status;
}
