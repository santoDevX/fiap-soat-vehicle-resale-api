package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos;

import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateVehicleRequest(
        @NotBlank
        @Size(max = 100)
        String brand,

        @NotBlank
        @Size(max = 100)
        String model,

        @NotBlank
        @Size(max = 50)
        String color,

        @NotNull
        @Min(1886)
        Integer year,

        @Size(max = 250)
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
