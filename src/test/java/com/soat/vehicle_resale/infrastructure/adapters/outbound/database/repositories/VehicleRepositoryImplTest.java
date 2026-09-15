package com.soat.vehicle_resale.infrastructure.adapters.outbound.database.repositories;

import com.soat.vehicle_resale.TestcontainersConfiguration;
import com.soat.vehicle_resale.core.domain.models.Vehicle;
import com.soat.vehicle_resale.core.domain.models.VehicleStatus;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.entities.VehicleEntity;
import com.soat.vehicle_resale.infrastructure.adapters.outbound.database.mappers.VehicleEntityMapperImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, VehicleEntityMapperImpl.class, VehicleRepositoryImpl.class})
class VehicleRepositoryImplTest {

    @Autowired
    private VehicleRepositoryImpl repository;

    @Autowired
    private TestEntityManager entityManager;

    private VehicleEntity persistedVehicleEntity(String brand, VehicleStatus status, BigDecimal price) {
        var entity = new VehicleEntity();
        entity.setBrand(brand);
        entity.setModel("Uno");
        entity.setColor("White");
        entity.setYear(2020);
        entity.setDescription("desc");
        entity.setPrice(price);
        entity.setStatus(status);
        return entityManager.persistFlushFind(entity);
    }

    @Test
    void shouldPersistAndReturnVehicleWithGeneratedId_whenSaveIsCalled() {
        var toSave = new Vehicle(null, "Fiat", "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000), VehicleStatus.AVAILABLE);

        var saved = repository.save(toSave);

        assertThat(saved.id()).isNotNull();
        var persisted = entityManager.find(VehicleEntity.class, saved.id());
        assertThat(persisted.getBrand()).isEqualTo("Fiat");
        assertThat(persisted.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateFieldsButKeepPersistedStatus_whenUpdateIsCalled() {
        var persisted = persistedVehicleEntity("Fiat", VehicleStatus.AVAILABLE, BigDecimal.valueOf(50000));
        entityManager.clear();

        var toUpdate = new Vehicle(persisted.getId(), "Ford", "Ka", "Black", 2021, "new desc",
                BigDecimal.valueOf(60000), VehicleStatus.SOLD);

        var updated = repository.update(toUpdate);

        assertThat(updated.brand()).isEqualTo("Ford");
        assertThat(updated.model()).isEqualTo("Ka");
        assertThat(updated.color()).isEqualTo("Black");
        assertThat(updated.year()).isEqualTo(2021);
        assertThat(updated.description()).isEqualTo("new desc");
        assertThat(updated.price()).isEqualByComparingTo(BigDecimal.valueOf(60000));
        assertThat(updated.status()).isEqualTo(VehicleStatus.AVAILABLE);

        entityManager.flush();
        entityManager.clear();
        var reloaded = entityManager.find(VehicleEntity.class, persisted.getId());
        assertThat(reloaded.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(reloaded.getBrand()).isEqualTo("Ford");
    }

    @Test
    void shouldReturnOnlyVehiclesWithGivenStatusOrderedByPriceAsc_whenFindByStatusIsCalled() {
        persistedVehicleEntity("Expensive", VehicleStatus.AVAILABLE, BigDecimal.valueOf(90000));
        persistedVehicleEntity("Cheap", VehicleStatus.AVAILABLE, BigDecimal.valueOf(30000));
        persistedVehicleEntity("Mid", VehicleStatus.AVAILABLE, BigDecimal.valueOf(60000));
        persistedVehicleEntity("SoldOne", VehicleStatus.SOLD, BigDecimal.valueOf(10000));

        var result = repository.findByStatus(VehicleStatus.AVAILABLE);

        assertThat(result).extracting(Vehicle::brand)
                .containsExactly("Cheap", "Mid", "Expensive");
    }

    @Test
    void shouldReturnVehicle_whenFindByIdForUpdateIsCalledWithExistingId() {
        var persisted = persistedVehicleEntity("Fiat", VehicleStatus.AVAILABLE, BigDecimal.valueOf(50000));

        var result = repository.findByIdForUpdate(persisted.getId());

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(persisted.getId());
        assertThat(result.get().brand()).isEqualTo("Fiat");
    }

    @Test
    void shouldReturnEmpty_whenFindByIdForUpdateIsCalledWithNonExistingId() {
        var result = repository.findByIdForUpdate(-1L);

        assertThat(result).isEmpty();
    }
}
