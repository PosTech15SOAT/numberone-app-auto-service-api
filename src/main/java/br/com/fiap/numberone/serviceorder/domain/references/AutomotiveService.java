package br.com.fiap.numberone.serviceorder.domain.references;

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
public class AutomotiveService {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String serviceType;
    private BigDecimal baseValue;
    private Integer estimatedTimeMinutes;
    private Boolean active;
}
