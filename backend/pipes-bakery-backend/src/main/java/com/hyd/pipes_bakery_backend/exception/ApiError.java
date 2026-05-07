package com.hyd.pipes_bakery_backend.exception;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Formato comun de error devuelto por la API.")
public class ApiError {

    @Schema(description = "Fecha y hora en la que se genero el error.", example = "2026-05-07T14:30:00")
    private final LocalDateTime timestamp;
    @Schema(description = "Codigo HTTP del error.", example = "400")
    private final int status;
    @Schema(description = "Texto asociado al estado HTTP.", example = "Bad Request")
    private final String error;
    @Schema(description = "Mensaje funcional del error.", example = "Validation failed")
    private final String message;
    @Schema(description = "Detalle de errores de validacion, si aplica.", example = "[\"Email should be valid\"]")
    private final List<String> details;

    public ApiError(int status, String error, String message, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public int getStatus() {
        return status;
    }
    public String getError() {
        return error;
    }
    public String getMessage() {
        return message;
    }
    public List<String> getDetails() {
        return details;
    }
}
