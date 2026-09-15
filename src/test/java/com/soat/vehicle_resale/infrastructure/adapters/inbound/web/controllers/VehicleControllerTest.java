package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soat.vehicle_resale.TestcontainersConfiguration;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.CreateVehicleRequest;
import com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos.UpdateVehicleRequest;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private VehicleEntity persistVehicle(VehicleStatus status, BigDecimal price) {
        var entity = new VehicleEntity();
        entity.setBrand("Fiat");
        entity.setModel("Uno");
        entity.setColor("White");
        entity.setYear(2020);
        entity.setDescription("desc");
        entity.setPrice(price);
        entity.setStatus(status);
        return vehicleJpaRepository.save(entity);
    }

    @Test
    void shouldCreateVehicle_whenRequesterHasAdminRole() throws Exception {
        var request = new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000));

        mockMvc.perform(post("/vehicles")
                        .with(jwtWithRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand").value("Fiat"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void shouldReturnUnauthorized_whenCreatingVehicleWithoutToken() throws Exception {
        var request = new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000));

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbidden_whenCreatingVehicleWithCustomerRole() throws Exception {
        var request = new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000));

        mockMvc.perform(post("/vehicles")
                        .with(jwtWithRole("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateVehicle_whenRequesterHasAdminRole() throws Exception {
        var persisted = persistVehicle(VehicleStatus.AVAILABLE, BigDecimal.valueOf(50000));
        var request = new UpdateVehicleRequest("Ford", "Ka", "Black", 2021, "new desc", BigDecimal.valueOf(60000));

        mockMvc.perform(put("/vehicles/{vehicleId}", persisted.getId())
                        .with(jwtWithRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Ford"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void shouldReturnUnauthorized_whenUpdatingVehicleWithoutToken() throws Exception {
        var persisted = persistVehicle(VehicleStatus.AVAILABLE, BigDecimal.valueOf(50000));
        var request = new UpdateVehicleRequest("Ford", "Ka", "Black", 2021, "new desc", BigDecimal.valueOf(60000));

        mockMvc.perform(put("/vehicles/{vehicleId}", persisted.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbidden_whenUpdatingVehicleWithCustomerRole() throws Exception {
        var persisted = persistVehicle(VehicleStatus.AVAILABLE, BigDecimal.valueOf(50000));
        var request = new UpdateVehicleRequest("Ford", "Ka", "Black", 2021, "new desc", BigDecimal.valueOf(60000));

        mockMvc.perform(put("/vehicles/{vehicleId}", persisted.getId())
                        .with(jwtWithRole("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistentVehicle() throws Exception {
        var request = new UpdateVehicleRequest("Ford", "Ka", "Black", 2021, "new desc", BigDecimal.valueOf(60000));

        mockMvc.perform(put("/vehicles/{vehicleId}", 999999L)
                        .with(jwtWithRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflict_whenUpdatingAlreadySoldVehicle() throws Exception {
        var persisted = persistVehicle(VehicleStatus.SOLD, BigDecimal.valueOf(50000));
        var request = new UpdateVehicleRequest("Ford", "Ka", "Black", 2021, "new desc", BigDecimal.valueOf(60000));

        mockMvc.perform(put("/vehicles/{vehicleId}", persisted.getId())
                        .with(jwtWithRole("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldListAvailableVehiclesByDefault_whenNoStatusProvided() throws Exception {
        persistVehicle(VehicleStatus.AVAILABLE, BigDecimal.valueOf(50000));
        persistVehicle(VehicleStatus.SOLD, BigDecimal.valueOf(30000));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void shouldListVehiclesByGivenStatus_whenStatusProvided() throws Exception {
        persistVehicle(VehicleStatus.AVAILABLE, BigDecimal.valueOf(50000));
        persistVehicle(VehicleStatus.SOLD, BigDecimal.valueOf(30000));

        mockMvc.perform(get("/vehicles").param("status", "SOLD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("SOLD"));
    }
}
