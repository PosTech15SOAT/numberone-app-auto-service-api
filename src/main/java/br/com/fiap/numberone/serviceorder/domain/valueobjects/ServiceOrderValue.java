package br.com.fiap.numberone.serviceorder.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderValue {
    public UUID serviceOrderId;
    public BigDecimal totalValue;
}
