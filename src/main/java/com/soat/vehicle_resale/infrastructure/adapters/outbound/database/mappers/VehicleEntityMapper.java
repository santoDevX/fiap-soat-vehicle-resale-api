package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.mappers;

import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

// componentModel = "spring" faz o MapStruct gerar a implementacao ja como @Component,
// para poder ser injetada no adapter que implementa VehicleRepositoryPort
@Mapper(componentModel = "spring")
public interface VehicleEntityMapper {

    VehicleEntity fromDomain(Vehicle vehicle);

    Vehicle fromEntity(VehicleEntity vehicleEntity);

    // status nao pode ser alterado via PUT /vehicles/{id} (so via fluxo de compra),
    // por isso fica ignorado aqui: a entity gerenciada mantem o status ja carregado do banco
    // e o service nao precisa fazer uma leitura extra so pra preservar esse campo
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDomain(Vehicle vehicle, @MappingTarget VehicleEntity entity);
}
