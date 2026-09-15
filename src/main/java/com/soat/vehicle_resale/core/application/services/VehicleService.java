package com.soat.vehicle_resale.core.application.services;

import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleCreateUseCase;
import com.soat.vehicle_resale.core.application.ports.inbound.usecase.VehicleFindUseCase;
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
        VehicleFindUseCase {

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

        // o status atual e preservado pelo mapper que ignora o campo status.
        return repository.update(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> find(VehicleStatus status) {
        var effectiveStatus = status != null ? status : VehicleStatus.AVAILABLE;
        return repository.findByStatus(effectiveStatus);
    }
}
