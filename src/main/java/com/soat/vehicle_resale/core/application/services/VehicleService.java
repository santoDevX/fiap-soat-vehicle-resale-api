package com.soat.vehicle_resale.core.application.services;

import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleCreateUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleFindAvailableUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleFindSoldUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleUpdateUseCase;
import com.soat.vehicle_resale.core.application.ports.outbound.VehicleRepositoryPort;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleAlreadySoldException;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleNotFoundException;
import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class VehicleService implements VehicleCreateUseCase,
        VehicleUpdateUseCase,
        VehicleFindAvailableUseCase,
        VehicleFindSoldUseCase {

    private final VehicleRepositoryPort repository;

    @Override
    @Transactional
    public Vehicle create(Vehicle vehicle) {
        return repository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle update(Vehicle vehicle) {
        // lock pessimista evita que um PUT concorrente com uma compra (SaleService.purchase)
        // altere o veiculo entre a checagem de status e a escrita.
        var current = repository.findByIdForUpdate(vehicle.id())
                .orElseThrow(VehicleNotFoundException::new);

        if (current.status() == VehicleStatus.SOLD) {
            throw new VehicleAlreadySoldException();
        }

        // o status atual e preservado pelo mapper (VehicleEntityMapper.updateEntityFromDomain
        // ignora o campo status).
        return repository.update(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailable() {
        return repository.findByStatus(VehicleStatus.AVAILABLE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findSold() {
        return repository.findByStatus(VehicleStatus.SOLD);
    }
}
