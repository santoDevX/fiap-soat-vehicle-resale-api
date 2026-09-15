package com.soat.vehicle_resale.core.application.services;

import com.soat.vehicle_resale.core.application.ports.inbound.usecase.SalePurchaseUseCase;
import com.soat.vehicle_resale.core.application.ports.outbound.SaleRepositoryPort;
import com.soat.vehicle_resale.core.application.ports.outbound.VehicleRepositoryPort;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleAlreadySoldException;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleNotFoundException;
import com.soat.vehicle_resale.core.domain.models.Sale;
import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SaleService implements SalePurchaseUseCase {

    private final SaleRepositoryPort saleRepository;
    private final VehicleRepositoryPort vehicleRepository;

    @Override
    @Transactional
    public Sale purchase(Long vehicleId, String buyerId) {
        var vehicle = vehicleRepository.findByIdForUpdate(vehicleId)
                .orElseThrow(VehicleNotFoundException::new);

        if (vehicle.status() == VehicleStatus.SOLD) {
            throw new VehicleAlreadySoldException();
        }

        var saleToSave = new Sale(null, vehicleId, buyerId, vehicle.price(), null);

        var vehicleToSave = new Vehicle(vehicle.id(),
                vehicle.brand(),
                vehicle.model(),
                vehicle.color(),
                vehicle.year(),
                vehicle.description(),
                vehicle.price(),
                VehicleStatus.SOLD);

        vehicleRepository.save(vehicleToSave);

        return saleRepository.save(saleToSave);
    }
}
