package com.soat.vehicle_resale.core.application.services;

import com.soat.vehicle_resale.core.application.ports.outbound.VehicleRepositoryPort;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleAlreadySoldException;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleNotFoundException;
import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepositoryPort repository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle(Long id, VehicleStatus status) {
        return new Vehicle(id, "Fiat", "Uno", "White", 2020, "desc", BigDecimal.TEN, status);
    }

    @Test
    void shouldCreateVehicle_whenCreateIsCalled() {
        var toCreate = vehicle(null, VehicleStatus.AVAILABLE);
        var saved = vehicle(1L, VehicleStatus.AVAILABLE);
        when(repository.save(toCreate)).thenReturn(saved);

        var result = vehicleService.create(toCreate);

        assertThat(result).isEqualTo(saved);
        verify(repository).save(toCreate);
    }

    @Test
    void shouldUpdateVehicle_whenVehicleExistsAndIsNotSold() {
        var current = vehicle(1L, VehicleStatus.AVAILABLE);
        var toUpdate = vehicle(1L, VehicleStatus.AVAILABLE);
        var updated = vehicle(1L, VehicleStatus.AVAILABLE);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(current));
        when(repository.update(toUpdate)).thenReturn(updated);

        var result = vehicleService.update(toUpdate);

        assertThat(result).isEqualTo(updated);
        verify(repository).update(toUpdate);
    }

    @Test
    void shouldThrowVehicleNotFoundException_whenUpdatingNonExistentVehicle() {
        var toUpdate = vehicle(1L, VehicleStatus.AVAILABLE);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(toUpdate))
                .isInstanceOf(VehicleNotFoundException.class);

        verify(repository, never()).update(any());
    }

    @Test
    void shouldThrowVehicleAlreadySoldException_whenUpdatingSoldVehicle() {
        var current = vehicle(1L, VehicleStatus.SOLD);
        var toUpdate = vehicle(1L, VehicleStatus.SOLD);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(current));

        assertThatThrownBy(() -> vehicleService.update(toUpdate))
                .isInstanceOf(VehicleAlreadySoldException.class);

        verify(repository, never()).update(any());
    }

    @ParameterizedTest
    @EnumSource(VehicleStatus.class)
    void shouldFindByGivenStatus_whenStatusIsProvided(VehicleStatus status) {
        var expected = List.of(vehicle(1L, status));
        when(repository.findByStatus(status)).thenReturn(expected);

        var result = vehicleService.find(status);

        assertThat(result).isEqualTo(expected);
        verify(repository, times(1)).findByStatus(status);
    }

    @Test
    void shouldFindByAvailableStatus_whenStatusIsNull() {
        var expected = List.of(vehicle(1L, VehicleStatus.AVAILABLE));
        when(repository.findByStatus(VehicleStatus.AVAILABLE)).thenReturn(expected);

        var result = vehicleService.find(null);

        assertThat(result).isEqualTo(expected);
        verify(repository).findByStatus(VehicleStatus.AVAILABLE);
    }
}
