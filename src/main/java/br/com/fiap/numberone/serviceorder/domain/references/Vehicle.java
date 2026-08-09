package br.com.fiap.numberone.serviceorder.domain.references;

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
public class Vehicle {

    private UUID id;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private UUID customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
