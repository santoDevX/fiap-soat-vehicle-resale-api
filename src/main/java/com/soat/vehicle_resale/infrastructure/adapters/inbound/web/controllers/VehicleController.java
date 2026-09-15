package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleCreateUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleFindUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleUpdateUseCase;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.CreateVehicleRequest;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.UpdateVehicleRequest;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.VehicleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicles", description = "Cadastro, atualização e consulta de veículos")
public class VehicleController {

    private final VehicleCreateUseCase createUseCase;
    private final VehicleUpdateUseCase updateUseCase;
    private final VehicleFindUseCase findUseCase;

    @Operation(
            summary = "Cadastrar veículo",
            description = "Cria um novo veículo com status inicial AVAILABLE. Requer role ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        var vehicle = createUseCase.create(request.toDomain());
        var response = VehicleResponse.fromDomain(vehicle);
        return ResponseEntity.created(URI.create("/vehicles/" + vehicle.id())).body(response);
    }

    @Operation(
            summary = "Atualizar veículo",
            description = "Atualiza os dados de um veículo existente pelo seu ID. Requer role ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Veículo já vendido", content = @Content)
    })
    @PutMapping("/{vehicleId}")
    public VehicleResponse update(
            @Parameter(description = "ID do veículo") @PathVariable Long vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request) {

        var vehicle = updateUseCase.update(request.toDomain(vehicleId));
        return VehicleResponse.fromDomain(vehicle);
    }

    @Operation(summary = "Listar veículos", description = "Lista veículos, opcionalmente filtrando por status.")
    @ApiResponse(responseCode = "200", description = "Lista de veículos retornada com sucesso")
    @GetMapping
    public List<VehicleResponse> find(
            @Parameter(description = "Status para filtrar os veículos (ex: AVAILABLE, SOLD)")
            @RequestParam(required = false)
            VehicleStatus status) {
        return findUseCase.find(status).stream()
                .map(VehicleResponse::fromDomain)
                .toList();
    }
}
