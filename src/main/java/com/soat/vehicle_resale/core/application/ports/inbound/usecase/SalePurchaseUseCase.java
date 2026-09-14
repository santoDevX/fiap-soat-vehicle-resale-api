package com.soat.vehicle_resale.core.application.ports.inbound.usecase;

import com.soat.vehicle_resale.core.domain.models.Sale;

public interface SalePurchaseUseCase {
    Sale purchase(Long vehicleId, String buyerId);
}
