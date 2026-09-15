package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories.jpa;

import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, Long> {
}
