package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.mappers;

import com.soat.vehicle_resale.core.domain.models.Sale;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.SaleEntity;
import org.mapstruct.Mapper;

// componentModel = "spring" faz o MapStruct gerar a implementacao ja como @Component,
// para poder ser injetada no adapter que implementa SaleRepositoryPort
@Mapper(componentModel = "spring")
public interface SaleEntityMapper {

    SaleEntity fromDomain(Sale sale);

    Sale fromEntity(SaleEntity saleEntity);
}
