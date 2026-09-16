package com.soat.vehicle_resale.infrastructure.adapters.inbound.web.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CreateVehicleRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static CreateVehicleRequest validRequest() {
        return new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000));
    }

    @Test
    void shouldHaveNoViolations_whenAllFieldsAreValid() {
        var violations = validator.validate(validRequest());

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequests")
    void shouldRejectInvalidField_whenFieldViolatesConstraint(String scenario, CreateVehicleRequest request, String expectedField) {
        var violations = validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains(expectedField);
    }

    static Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of("brand null", new CreateVehicleRequest(null, "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000)), "brand"),
                Arguments.of("brand blank", new CreateVehicleRequest("  ", "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000)), "brand"),
                Arguments.of("brand too long", new CreateVehicleRequest("a".repeat(101), "Uno", "White", 2020, "desc", BigDecimal.valueOf(50000)), "brand"),
                Arguments.of("model null", new CreateVehicleRequest("Fiat", null, "White", 2020, "desc", BigDecimal.valueOf(50000)), "model"),
                Arguments.of("model blank", new CreateVehicleRequest("Fiat", "  ", "White", 2020, "desc", BigDecimal.valueOf(50000)), "model"),
                Arguments.of("model too long", new CreateVehicleRequest("Fiat", "a".repeat(101), "White", 2020, "desc", BigDecimal.valueOf(50000)), "model"),
                Arguments.of("color null", new CreateVehicleRequest("Fiat", "Uno", null, 2020, "desc", BigDecimal.valueOf(50000)), "color"),
                Arguments.of("color blank", new CreateVehicleRequest("Fiat", "Uno", "  ", 2020, "desc", BigDecimal.valueOf(50000)), "color"),
                Arguments.of("color too long", new CreateVehicleRequest("Fiat", "Uno", "a".repeat(51), 2020, "desc", BigDecimal.valueOf(50000)), "color"),
                Arguments.of("year null", new CreateVehicleRequest("Fiat", "Uno", "White", null, "desc", BigDecimal.valueOf(50000)), "year"),
                Arguments.of("year below minimum", new CreateVehicleRequest("Fiat", "Uno", "White", 1885, "desc", BigDecimal.valueOf(50000)), "year"),
                Arguments.of("description too long", new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "a".repeat(251), BigDecimal.valueOf(50000)), "description"),
                Arguments.of("price null", new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "desc", null), "price"),
                Arguments.of("price zero", new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "desc", BigDecimal.ZERO), "price"),
                Arguments.of("price negative", new CreateVehicleRequest("Fiat", "Uno", "White", 2020, "desc", BigDecimal.valueOf(-1)), "price")
        );
    }
}
