package com.soat.vehicle_resale.core.domain.exceptions;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String message) {
        super(message);
    }

    public VehicleNotFoundException() {
        super("Vehicle not found");
    }
}
