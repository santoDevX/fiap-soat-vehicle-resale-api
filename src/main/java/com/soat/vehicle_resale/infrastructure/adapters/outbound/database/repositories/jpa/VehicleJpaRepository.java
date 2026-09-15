package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories.jpa;

import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.VehicleEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, Long> {

    List<VehicleEntity> findByStatusOrderByPriceAsc(VehicleStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from VehicleEntity v where v.id = :id")
    Optional<VehicleEntity> findByIdForUpdate(@Param("id") Long id);
}
