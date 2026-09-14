package com.soat.vehicle_resale.core.domain.models;

import java.math.BigDecimal;

public record Vehicle(
        Long id,
        String brand,
        String model,
        String color,
        Integer year,
        String description,
        BigDecimal price,
        VehicleStatus status
) {
}
