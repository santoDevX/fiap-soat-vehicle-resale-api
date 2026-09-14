package com.soat.vehicle_resale.infrastructure.adapters.inbound.web;

import com.soat.vehicle_resale.core.domain.exceptions.VehicleAlreadySoldException;
import com.soat.vehicle_resale.core.domain.exceptions.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(VehicleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(VehicleAlreadySoldException.class)
    public ResponseEntity<ApiError> handleAlreadySold(VehicleAlreadySoldException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // DTO local do handler, nao usa o nome ErrorResponse pra nao colidir com org.springframework.web.ErrorResponse
    public record ApiError(Instant timestamp, int status, String error, String message) {
        static ApiError of(HttpStatus status, String message) {
            return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message);
        }
    }
}
