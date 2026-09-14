package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos;

import com.soat.vehicle_resale.core.domain.models.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponse(
        Long saleId,
        Long vehicleId,
        String buyerId,
        BigDecimal totalAmount,
        LocalDateTime soldAt
) {
    public static PurchaseResponse fromDomain(Sale sale) {
        return new PurchaseResponse(
                sale.id(),
                sale.vehicleId(),
                sale.buyerId(),
                sale.totalAmount(),
                sale.createdAt()
        );
    }
}
