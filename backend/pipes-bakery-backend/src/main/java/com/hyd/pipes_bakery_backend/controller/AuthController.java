package com.hyd.pipes_bakery_backend.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.auth.AuthResponseDTO;
import com.hyd.pipes_bakery_backend.dto.auth.LoginRequestDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.AuthService;
import com.hyd.pipes_bakery_backend.service.LoginRateLimitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Inicio de sesion administrativo y emision de tokens JWT.")
public class AuthController {

    public static final String ADMIN_AUTH_COOKIE_NAME = "ADMIN_AUTH_TOKEN";
    private static final Duration ADMIN_AUTH_COOKIE_MAX_AGE = Duration.ofHours(1);

    private final AuthService authService;
    private final LoginRateLimitService loginRateLimitService;

    public AuthController(AuthService authService, LoginRateLimitService loginRateLimitService) {
        this.authService = authService;
        this.loginRateLimitService = loginRateLimitService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion como administrador", description = "Valida credenciales de administrador y crea una cookie HttpOnly Secure con el JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario sin rol administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public AuthResponseDTO login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = resolveClientIp(httpRequest);

        loginRateLimitService.assertLoginAllowed(ipAddress, request.getEmail());

        String token;
        try {
            token = authService.login(request);
            loginRateLimitService.clearFailedAttempts(ipAddress, request.getEmail());
        } catch (BadCredentialsException ex) {
            loginRateLimitService.recordFailedAttempt(ipAddress, request.getEmail());
            throw ex;
        }

        response.addHeader(HttpHeaders.SET_COOKIE, buildAdminAuthCookie(token, ADMIN_AUTH_COOKIE_MAX_AGE).toString());

        return new AuthResponseDTO(true);
    }

    @GetMapping("/session")
    @Operation(summary = "Comprobar sesion administrativa", description = "Devuelve estado autenticado cuando la cookie JWT administrativa es valida.")
    public AuthResponseDTO session() {
        return new AuthResponseDTO(true);
    }

    @GetMapping("/csrf")
    @Operation(summary = "Obtener token CSRF administrativo", description = "Devuelve el token CSRF que debe enviarse en mutaciones administrativas.")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName(),
                "token", csrfToken.getToken()
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion administrativa", description = "Elimina la cookie JWT administrativa.")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, buildAdminAuthCookie("", Duration.ZERO).toString())
                .build();
    }

    private ResponseCookie buildAdminAuthCookie(String token, Duration maxAge) {
        return ResponseCookie.from(ADMIN_AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
