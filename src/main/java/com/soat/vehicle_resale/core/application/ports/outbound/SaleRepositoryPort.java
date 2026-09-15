package com.soat.vehicle_resale.core.application.ports.outbound;

import com.soat.vehicle_resale.core.domain.models.Sale;

public interface SaleRepositoryPort {
    Sale save(Sale sale);
}
