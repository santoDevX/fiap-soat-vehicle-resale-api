package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories.jpa;

import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, Long> {

    List<VehicleEntity> findByStatusOrderByPriceAsc(VehicleStatus status);
}
