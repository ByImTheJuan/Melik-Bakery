package com.hyd.pipes_bakery_backend.controller;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.auth.LoginRequestDTO;
import com.hyd.pipes_bakery_backend.config.JwtProperties;
import com.hyd.pipes_bakery_backend.service.AuthService;
import com.hyd.pipes_bakery_backend.service.JwtRevocationService;
import com.hyd.pipes_bakery_backend.service.JwtService;
import com.hyd.pipes_bakery_backend.service.LoginRateLimitService;
import java.time.Duration;

@SuppressWarnings("null")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private LoginRateLimitService loginRateLimitService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtRevocationService jwtRevocationService;

    @MockitoBean
    private CsrfTokenRepository csrfTokenRepository;

    @Test
    void shouldSetJwtInHttpOnlySecureCookieWhenLoginSucceeds() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@melikbakery.com");
        request.setPassword("plain-password");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn("jwt-token");
        when(jwtProperties.getExpiration()).thenReturn(Duration.ofMinutes(30));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("ADMIN_AUTH_TOKEN=jwt-token")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=1800")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")));

        verify(loginRateLimitService).assertLoginAllowed(anyString(), anyString());
        verify(loginRateLimitService).clearFailedAttempts(anyString(), anyString());
    }

    @Test
    void shouldNotTrustClientProvidedForwardedForWhenRateLimitingLogin() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@melikbakery.com");
        request.setPassword("plain-password");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn("jwt-token");
        when(jwtProperties.getExpiration()).thenReturn(Duration.ofMinutes(30));

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "203.0.113.10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(loginRateLimitService).assertLoginAllowed(eq("127.0.0.1"), eq("admin@melikbakery.com"));
    }

    @Test
    void shouldClearJwtCookieOnLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("ADMIN_AUTH_TOKEN=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")));
    }

    @Test
    void shouldRevokeBearerJwtOnLogout() throws Exception {
        when(jwtService.getRemainingValidity("bearer-token")).thenReturn(Duration.ofMinutes(10));

        mockMvc.perform(post("/api/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer bearer-token"))
                .andExpect(status().isNoContent());

        verify(jwtRevocationService).revoke("bearer-token", Duration.ofMinutes(10));
    }
}
