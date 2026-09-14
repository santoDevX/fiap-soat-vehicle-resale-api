package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.soat.vehicle_resale.core.application.services.SaleService;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.PurchaseResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicles")
public class SaleController {

    private final SaleService service;

    @PostMapping("/{vehicleId}/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse purchase(
            @PathVariable Long vehicleId
//            @AuthenticationPrincipal Jwt jwt
    ) {

        // String buyerId = jwt.getSubject();

        var sale = service.purchase(vehicleId, UUID.randomUUID().toString());

        return PurchaseResponse.fromDomain(sale);
    }
}
