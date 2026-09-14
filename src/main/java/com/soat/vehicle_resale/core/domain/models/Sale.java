package com.soat.vehicle_resale.core.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Sale(
        Long id,
        Long vehicleId,
        String buyerId,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
