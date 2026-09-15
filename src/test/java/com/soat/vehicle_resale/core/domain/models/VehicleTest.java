package com.soat.vehicle_resale.core.domain.models;

import com.soat.vehicle_resale.core.domain.exceptions.InvalidVehicleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Year;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class VehicleTest {

    private static int currentYear() {
        return Year.now(ZoneId.of("UTC")).getValue();
    }

    @Test
    void shouldCreateVehicle_whenYearIsInThePast() {
        assertThatNoException().isThrownBy(() ->
                new Vehicle(1L, "Fiat", "Uno", "White", 1990, "desc", BigDecimal.TEN, VehicleStatus.AVAILABLE));
    }

    @Test
    void shouldCreateVehicle_whenYearIsNull() {
        assertThatNoException().isThrownBy(() ->
                new Vehicle(1L, "Fiat", "Uno", "White", null, "desc", BigDecimal.TEN, VehicleStatus.AVAILABLE));
    }

    @Test
    void shouldCreateVehicle_whenYearIsAtTheAllowedLimit() {
        int limitYear = currentYear() + 1;

        assertThatNoException().isThrownBy(() ->
                new Vehicle(1L, "Fiat", "Uno", "White", limitYear, "desc", BigDecimal.TEN, VehicleStatus.AVAILABLE));
    }

    @Test
    void shouldThrowInvalidVehicleException_whenYearIsBeyondTheAllowedLimit() {
        int invalidYear = currentYear() + 2;

        assertThatThrownBy(() ->
                new Vehicle(1L, "Fiat", "Uno", "White", invalidYear, "desc", BigDecimal.TEN, VehicleStatus.AVAILABLE))
                .isInstanceOf(InvalidVehicleException.class)
                .hasMessage("Vehicle year cannot be in the far future");
    }

    @Test
    void shouldExposeFieldsThroughRecordAccessors() {
        var vehicle = new Vehicle(1L, "Fiat", "Uno", "White", 2020, "desc", BigDecimal.TEN, VehicleStatus.AVAILABLE);

        assertThat(vehicle.id()).isEqualTo(1L);
        assertThat(vehicle.brand()).isEqualTo("Fiat");
        assertThat(vehicle.model()).isEqualTo("Uno");
        assertThat(vehicle.color()).isEqualTo("White");
        assertThat(vehicle.year()).isEqualTo(2020);
        assertThat(vehicle.description()).isEqualTo("desc");
        assertThat(vehicle.price()).isEqualTo(BigDecimal.TEN);
        assertThat(vehicle.status()).isEqualTo(VehicleStatus.AVAILABLE);
    }
}
