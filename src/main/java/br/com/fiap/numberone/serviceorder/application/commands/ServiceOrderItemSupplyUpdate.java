package br.com.fiap.numberone.serviceorder.application.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderItemSupplyUpdate {

    private UUID inventoryItemId;
    private Integer quantityUsed;
}
