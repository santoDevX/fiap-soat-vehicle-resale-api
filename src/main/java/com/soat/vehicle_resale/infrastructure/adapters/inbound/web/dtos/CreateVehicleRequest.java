package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos;

import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateVehicleRequest(
        @NotBlank
        String brand,

        @NotBlank
        String model,

        @NotBlank
        String color,

        @NotNull
        @Min(1886)
        Integer year,

        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal price
) {
    public Vehicle toDomain() {
        return new Vehicle(
                null,
                this.brand(),
                this.model(),
                this.color(),
                this.year(),
                this.description(),
                this.price(),
                VehicleStatus.AVAILABLE
        );
    }
}
