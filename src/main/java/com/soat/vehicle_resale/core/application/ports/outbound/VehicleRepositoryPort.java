package com.soat.vehicle_resale.core.application.ports.outbound;

import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;

import java.util.List;
import java.util.Optional;

public interface VehicleRepositoryPort {
    Vehicle save(Vehicle vehicle);

    Vehicle update(Vehicle vehicle);

    List<Vehicle> findByStatus(VehicleStatus status);

    Optional<Vehicle> findByIdForUpdate(Long id);
}
