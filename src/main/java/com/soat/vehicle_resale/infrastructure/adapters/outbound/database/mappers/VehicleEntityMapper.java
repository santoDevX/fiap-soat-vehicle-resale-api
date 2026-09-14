package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.mappers;

import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.VehicleEntity;
import org.mapstruct.Mapper;

// componentModel = "spring" faz o MapStruct gerar a implementacao ja como @Component,
// para poder ser injetada no adapter que implementa VehicleRepositoryPort
@Mapper(componentModel = "spring")
public interface VehicleEntityMapper {

    VehicleEntity fromDomain(Vehicle vehicle);

    Vehicle fromEntity(VehicleEntity vehicleEntity);
}
