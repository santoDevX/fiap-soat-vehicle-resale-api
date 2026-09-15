package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories;

import com.soat.vehicle_resale.TestcontainersConfiguration;
import com.soat.vehicle_resale.core.domain.models.Sale;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.SaleEntity;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.VehicleEntity;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.mappers.SaleEntityMapperImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, SaleEntityMapperImpl.class, SaleRepositoryImpl.class})
class SaleRepositoryImplTest {

    @Autowired
    private SaleRepositoryImpl repository;

    @Autowired
    private TestEntityManager entityManager;

    private Long persistVehicle() {
        var entity = new VehicleEntity();
        entity.setBrand("Fiat");
        entity.setModel("Uno");
        entity.setColor("White");
        entity.setYear(2020);
        entity.setDescription("desc");
        entity.setPrice(BigDecimal.valueOf(50000));
        entity.setStatus(VehicleStatus.SOLD);
        return entityManager.persistFlushFind(entity).getId();
    }

    @Test
    void shouldPersistAndReturnSaleWithGeneratedId_whenSaveIsCalled() {
        var vehicleId = persistVehicle();
        var toSave = new Sale(null, vehicleId, "buyer-1", BigDecimal.valueOf(50000), null);

        var saved = repository.save(toSave);

        assertThat(saved.id()).isNotNull();
        var persisted = entityManager.find(SaleEntity.class, saved.id());
        assertThat(persisted.getVehicleId()).isEqualTo(vehicleId);
        assertThat(persisted.getBuyerId()).isEqualTo("buyer-1");
        assertThat(persisted.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectSecondSale_whenVehicleIdIsAlreadySold() {
        var vehicleId = persistVehicle();
        entityManager.persistFlushFind(new SaleEntity(null, vehicleId, "buyer-1",
                BigDecimal.valueOf(50000), null, null));

        var duplicate = new Sale(null, vehicleId, "buyer-2", BigDecimal.valueOf(50000), null);

        assertThatThrownBy(() -> repository.save(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
