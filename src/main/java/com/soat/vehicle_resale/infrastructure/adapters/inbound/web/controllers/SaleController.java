package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.soat.vehicle_resale.core.application.ports.inbound.usecase.SalePurchaseUseCase;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.PurchaseResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicles")
public class SaleController {

    private final SalePurchaseUseCase purchaseUseCase;

    @PostMapping("/{vehicleId}/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse purchase(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String buyerId = jwt.getSubject();

        var sale = purchaseUseCase.purchase(vehicleId, buyerId);
        return PurchaseResponse.fromDomain(sale);
    }
}
