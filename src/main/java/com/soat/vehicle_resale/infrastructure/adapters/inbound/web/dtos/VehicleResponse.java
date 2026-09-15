package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos;

import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;

import java.math.BigDecimal;

public record VehicleResponse(
        Long id,
        String brand,
        String model,
        String color,
        Integer year,
        String description,
        BigDecimal price,
        VehicleStatus status
) {
    public static VehicleResponse fromDomain(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.id(),
                vehicle.brand(),
                vehicle.model(),
                vehicle.color(),
                vehicle.year(),
                vehicle.description(),
                vehicle.price(),
                vehicle.status()
        );
    }
}
