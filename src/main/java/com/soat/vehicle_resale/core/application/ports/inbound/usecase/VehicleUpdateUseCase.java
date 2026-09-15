package com.soat.vehicle_resale.core.application.ports.inbound.usecase;

import com.soat.vehicle_resale.core.domain.models.Vehicle;

public interface VehicleUpdateUseCase {
    Vehicle update(Vehicle vehicle);
}
