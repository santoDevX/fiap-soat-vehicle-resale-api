package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleCreateUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleFindAvailableUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleFindSoldUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleUpdateUseCase;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.CreateVehicleRequest;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.UpdateVehicleRequest;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.VehicleResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleCreateUseCase createUseCase;
    private final VehicleUpdateUseCase updateUseCase;
    private final VehicleFindAvailableUseCase findAvailableUseCase;
    private final VehicleFindSoldUseCase findSoldUseCase;

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        var vehicle = createUseCase.create(request.toDomain());
        var response = VehicleResponse.fromDomain(vehicle);
        return ResponseEntity.created(URI.create("/vehicles/" + vehicle.id())).body(response);
    }

    @PutMapping("/{vehicleId}")
    public VehicleResponse update(
            @PathVariable Long vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request) {

        var vehicle = updateUseCase.update(request.toDomain(vehicleId));
        return VehicleResponse.fromDomain(vehicle);
    }

    @GetMapping
    public List<VehicleResponse> findAvailable() {
        return findAvailableUseCase.findAvailable().stream()
                .map(VehicleResponse::fromDomain)
                .toList();
    }

    @GetMapping("/sold")
    public List<VehicleResponse> findSold() {
        return findSoldUseCase.findSold().stream()
                .map(VehicleResponse::fromDomain)
                .toList();
    }
}
