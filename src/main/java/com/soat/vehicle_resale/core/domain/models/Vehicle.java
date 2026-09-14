package com.soat.vehicle_resale.core.domain.models;

import java.math.BigDecimal;
import java.time.Year;
import java.time.ZoneId;

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
    public Vehicle {
        if (year != null && year > Year.now(ZoneId.of("UTC")).getValue() + 1) {
            throw new IllegalArgumentException("Vehicle year cannot be in the far future");
        }
    }
}
