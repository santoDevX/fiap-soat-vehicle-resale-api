package com.soat.vehicle_resale.core.domain.exceptions;

public class InvalidVehicleException extends RuntimeException {
    public InvalidVehicleException(String message) {
        super(message);
    }
}
