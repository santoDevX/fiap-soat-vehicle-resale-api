package com.soat.vehicle_resale.core.application.ports.inbound.usecase;

import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;

import java.util.List;

public interface VehicleFindUseCase {
    List<Vehicle> find(VehicleStatus status);
}
