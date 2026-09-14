package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories;

import com.soat.vehicle_resale.core.application.ports.outbound.VehicleRepositoryPort;
import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.mappers.VehicleEntityMapper;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories.jpa.VehicleJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class VehicleRepositoryImpl implements VehicleRepositoryPort {

    private final VehicleJpaRepository jpaRepository;
    private final VehicleEntityMapper mapper;

    @Override
    public Vehicle save(Vehicle vehicle) {
        var entity = mapper.fromDomain(vehicle);
        var saved = jpaRepository.save(entity);
        return mapper.fromEntity(saved);
    }

    @Override
    public List<Vehicle> findByStatus(VehicleStatus status) {
        return jpaRepository.findByStatusOrderByPriceAsc(status).stream()
                .map(mapper::fromEntity)
                .toList();
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::fromEntity);
    }
}
