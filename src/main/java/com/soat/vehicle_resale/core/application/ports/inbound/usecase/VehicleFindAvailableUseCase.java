package com.soat.vehicle_resale.core.application.ports.inbound.usecase;

import com.soat.vehicle_resale.core.domain.models.Vehicle;

import java.util.List;

public interface VehicleFindAvailableUseCase {
    List<Vehicle> findAvailable();
}
