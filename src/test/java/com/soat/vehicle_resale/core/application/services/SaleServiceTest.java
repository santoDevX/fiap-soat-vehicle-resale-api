package com.soat.vehicle_resale.core.application.services;

import com.soat.vehicle_resale.core.application.ports.outbound.SaleRepositoryPort;
import com.soat.vehicle_resale.core.application.ports.outbound.VehicleRepositoryPort;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleAlreadySoldException;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleNotFoundException;
import com.soat.vehicle_resale.core.domain.models.Sale;
import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepositoryPort saleRepository;

    @Mock
    private VehicleRepositoryPort vehicleRepository;

    @InjectMocks
    private SaleService saleService;

    private Vehicle vehicle(Long id, VehicleStatus status) {
        return new Vehicle(id, "Fiat", "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000), status);
    }

    @Test
    void shouldThrowVehicleNotFoundException_whenVehicleDoesNotExist() {
        when(vehicleRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.purchase(1L, "buyer-1"))
                .isInstanceOf(VehicleNotFoundException.class);

        verify(vehicleRepository, never()).save(any());
        verify(saleRepository, never()).save(any());
    }

    @Test
    void shouldThrowVehicleAlreadySoldException_whenVehicleIsAlreadySold() {
        when(vehicleRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(vehicle(1L, VehicleStatus.SOLD)));

        assertThatThrownBy(() -> saleService.purchase(1L, "buyer-1"))
                .isInstanceOf(VehicleAlreadySoldException.class);

        verify(vehicleRepository, never()).save(any());
        verify(saleRepository, never()).save(any());
    }

    @Test
    void shouldMarkVehicleAsSoldAndSaveSale_whenVehicleIsAvailable() {
        var available = vehicle(1L, VehicleStatus.AVAILABLE);
        when(vehicleRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(available));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var savedSale = new Sale(10L, 1L, "buyer-1", available.price(), null);
        when(saleRepository.save(any(Sale.class))).thenReturn(savedSale);

        var result = saleService.purchase(1L, "buyer-1");

        assertThat(result).isEqualTo(savedSale);

        var vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(vehicleCaptor.capture());
        assertThat(vehicleCaptor.getValue().status()).isEqualTo(VehicleStatus.SOLD);
        assertThat(vehicleCaptor.getValue().id()).isEqualTo(1L);

        var saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepository).save(saleCaptor.capture());
        assertThat(saleCaptor.getValue().vehicleId()).isEqualTo(1L);
        assertThat(saleCaptor.getValue().buyerId()).isEqualTo("buyer-1");
        assertThat(saleCaptor.getValue().totalAmount()).isEqualTo(available.price());
        assertThat(saleCaptor.getValue().id()).isNull();
    }
}
