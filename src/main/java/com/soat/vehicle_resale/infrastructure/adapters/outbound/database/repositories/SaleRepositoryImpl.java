package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories;

import com.soat.vehicle_resale.core.application.ports.outbound.SaleRepositoryPort;
import com.soat.vehicle_resale.core.domain.models.Sale;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.mappers.SaleEntityMapper;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories.jpa.SaleJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class SaleRepositoryImpl implements SaleRepositoryPort {

    private final SaleJpaRepository jpaRepository;
    private final SaleEntityMapper mapper;

    @Override
    public Sale save(Sale sale) {
        var entity = mapper.fromDomain(sale);
        var saved = jpaRepository.save(entity);
        return mapper.fromEntity(saved);
    }
}
