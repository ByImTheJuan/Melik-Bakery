package com.hyd.pipes_bakery_backend.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.auth.AuthResponseDTO;
import com.hyd.pipes_bakery_backend.dto.auth.LoginRequestDTO;
import com.hyd.pipes_bakery_backend.config.JwtProperties;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.AuthService;
import com.hyd.pipes_bakery_backend.service.JwtRevocationService;
import com.hyd.pipes_bakery_backend.service.JwtService;
import com.hyd.pipes_bakery_backend.service.LoginRateLimitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Inicio de sesion administrativo y emision de tokens JWT.")
public class AuthController {

    public static final String ADMIN_AUTH_COOKIE_NAME = "ADMIN_AUTH_TOKEN";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final LoginRateLimitService loginRateLimitService;
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;
    private final JwtRevocationService jwtRevocationService;
    private final CsrfTokenRepository csrfTokenRepository;

    public AuthController(
            AuthService authService,
            LoginRateLimitService loginRateLimitService,
            JwtProperties jwtProperties,
            JwtService jwtService,
            JwtRevocationService jwtRevocationService,
            CsrfTokenRepository csrfTokenRepository
    ) {
        this.authService = authService;
        this.loginRateLimitService = loginRateLimitService;
        this.jwtProperties = jwtProperties;
        this.jwtService = jwtService;
        this.jwtRevocationService = jwtRevocationService;
        this.csrfTokenRepository = csrfTokenRepository;
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

        csrfTokenRepository.saveToken(null, httpRequest, response);
        response.addHeader(HttpHeaders.SET_COOKIE, buildAdminAuthCookie(token, jwtProperties.getExpiration()).toString());

        return new AuthResponseDTO(true);
    }

    @GetMapping("/session")
    @Operation(summary = "Comprobar sesion administrativa", description = "Devuelve estado autenticado cuando la cookie JWT administrativa es valida.")
    public AuthResponseDTO session() {
        return new AuthResponseDTO(true);
    }

    @GetMapping("/session-status")
    @Operation(summary = "Consultar estado de sesion administrativa", description = "Devuelve el estado de sesion sin tratar la ausencia de autenticacion como un error.")
    public AuthResponseDTO sessionStatus(Authentication authentication) {
        boolean isAdmin = authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        return new AuthResponseDTO(isAdmin);
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
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = resolveAdminAuthToken(request);

        if (token != null) {
            try {
                jwtRevocationService.revoke(token, jwtService.getRemainingValidity(token));
            } catch (RuntimeException ignored) {
                // An invalid or expired cookie is still removed from the browser.
            }
        }

        csrfTokenRepository.saveToken(null, request, response);
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
        return request.getRemoteAddr();
    }

    private String resolveAdminAuthToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (ADMIN_AUTH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
