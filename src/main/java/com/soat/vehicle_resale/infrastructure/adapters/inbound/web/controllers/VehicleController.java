package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.soat.vehicle_resale.core.application.services.VehicleService;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.CreateVehicleRequest;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.UpdateVehicleRequest;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.VehicleResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(
            @Valid @RequestBody CreateVehicleRequest request) {

        var vehicle = service.create(request.toDomain());
        return VehicleResponse.fromDomain(vehicle);
    }

    @PutMapping("/{vehicleId}")
    public VehicleResponse update(
            @PathVariable Long vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request) {

        var vehicle = service.update(request.toDomain(vehicleId));
        return VehicleResponse.fromDomain(vehicle);
    }

    @GetMapping
    public List<VehicleResponse> findAvailable() {
        return service.findAvailable().stream()
                .map(VehicleResponse::fromDomain)
                .toList();
    }

    @GetMapping("/sold")
    public List<VehicleResponse> findSold() {
        return service.findSold().stream()
                .map(VehicleResponse::fromDomain)
                .toList();
    }
}
