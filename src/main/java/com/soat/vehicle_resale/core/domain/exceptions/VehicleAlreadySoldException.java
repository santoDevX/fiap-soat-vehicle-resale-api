package com.soat.vehicle_resale.core.domain.exceptions;

public class VehicleAlreadySoldException extends RuntimeException {
    public VehicleAlreadySoldException() {
        super("Vehicle already sold");
    }
}
