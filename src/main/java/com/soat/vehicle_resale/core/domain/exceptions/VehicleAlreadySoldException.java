package com.soat.vehicle_resale.core.domain.exceptions;

public class VehicleAlreadySoldException extends RuntimeException {
    public VehicleAlreadySoldException(String message) {
        super(message);
    }

    public VehicleAlreadySoldException() {
        super("Vehicle already sold");
    }
}
