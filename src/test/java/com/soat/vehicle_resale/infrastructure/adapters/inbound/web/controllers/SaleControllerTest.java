package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.soat.vehicle_resale.TestcontainersConfiguration;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.VehicleEntity;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories.jpa.SaleJpaRepository;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories.jpa.VehicleJpaRepository;
import com.soat.vehicle_resale.infrastructure.configuration.KeycloakRealmRoleConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleJpaRepository vehicleJpaRepository;

    @Autowired
    private SaleJpaRepository saleJpaRepository;

    @AfterEach
    void cleanUp() {
        saleJpaRepository.deleteAll();
        vehicleJpaRepository.deleteAll();
    }

    private RequestPostProcessor jwtWithRole(String role) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.claim("realm_access", Map.of("roles", List.of(role))))
                .authorities(new KeycloakRealmRoleConverter());
    }

    private VehicleEntity persistVehicle(VehicleStatus status) {
        var entity = new VehicleEntity();
        entity.setBrand("Fiat");
        entity.setModel("Uno");
        entity.setColor("White");
        entity.setYear(2020);
        entity.setDescription("desc");
        entity.setPrice(BigDecimal.valueOf(50000));
        entity.setStatus(status);
        return vehicleJpaRepository.save(entity);
    }

    @Test
    void shouldPurchaseVehicle_whenRequesterHasCustomerRoleAndVehicleIsAvailable() throws Exception {
        var vehicle = persistVehicle(VehicleStatus.AVAILABLE);

        mockMvc.perform(post("/vehicles/{vehicleId}/purchase", vehicle.getId())
                        .with(jwtWithRole("CUSTOMER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleId").value(vehicle.getId()))
                .andExpect(jsonPath("$.buyerId").exists())
                .andExpect(jsonPath("$.totalAmount").value(50000));
    }

    @Test
    void shouldReturnUnauthorized_whenPurchasingWithoutToken() throws Exception {
        var vehicle = persistVehicle(VehicleStatus.AVAILABLE);

        mockMvc.perform(post("/vehicles/{vehicleId}/purchase", vehicle.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbidden_whenPurchasingWithAdminRole() throws Exception {
        var vehicle = persistVehicle(VehicleStatus.AVAILABLE);

        mockMvc.perform(post("/vehicles/{vehicleId}/purchase", vehicle.getId())
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFound_whenPurchasingNonExistentVehicle() throws Exception {
        mockMvc.perform(post("/vehicles/{vehicleId}/purchase", 999999L)
                        .with(jwtWithRole("CUSTOMER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflict_whenPurchasingAlreadySoldVehicle() throws Exception {
        var vehicle = persistVehicle(VehicleStatus.SOLD);

        mockMvc.perform(post("/vehicles/{vehicleId}/purchase", vehicle.getId())
                        .with(jwtWithRole("CUSTOMER")))
                .andExpect(status().isConflict());
    }
}
