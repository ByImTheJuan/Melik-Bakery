package com.hyd.pipes_bakery_backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticacion administrativa.")
public class AuthResponseDTO {

    @Schema(description = "Indica si la sesion administrativa esta autenticada.", example = "true")
    private boolean authenticated;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
