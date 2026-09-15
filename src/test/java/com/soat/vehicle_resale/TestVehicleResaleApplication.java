package com.soat.vehicle_resale;

import org.springframework.boot.SpringApplication;

public class TestVehicleResaleApplication {

	public static void main(String[] args) {
		SpringApplication.from(VehicleResaleApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
