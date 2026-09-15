package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.soat.vehicle_resale.core.application.ports.inbound.usecase.SalePurchaseUseCase;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.PurchaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Sales", description = "Compra de veículos")
public class SaleController {

    private final SalePurchaseUseCase purchaseUseCase;

    @Operation(
            summary = "Comprar veículo",
            description = "Efetua a compra de um veículo disponível. O comprador é identificado pelo token JWT autenticado. Requer role CUSTOMER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compra realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Veículo já vendido", content = @Content)
    })
    @PostMapping("/{vehicleId}/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse purchase(
            @Parameter(description = "ID do veículo a ser comprado") @PathVariable Long vehicleId,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    ) {
        String buyerId = jwt.getSubject();

        var sale = purchaseUseCase.purchase(vehicleId, buyerId);
        return PurchaseResponse.fromDomain(sale);
    }
}
