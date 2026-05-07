package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hyd.pipes_bakery_backend.dto.auth.LoginRequestDTO;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ClientUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldReturnTokenWhenAdminCredentialsAreValid() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@melikbakery.com");
        request.setPassword("plain-password");

        UserDetails userDetails = User.withUsername("admin@melikbakery.com")
                .password("$2a$10$hashedPassword")
                .authorities("ROLE_ADMIN")
                .build();

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(passwordEncoder.matches("plain-password", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        String token = authService.login(request);

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void shouldRejectNonAdminUsers() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("user@melikbakery.com");
        request.setPassword("plain-password");

        UserDetails userDetails = User.withUsername("user@melikbakery.com")
                .password("$2a$10$hashedPassword")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(passwordEncoder.matches("plain-password", "$2a$10$hashedPassword")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldGenerateTokenUsingJwtService() {
        UserDetails userDetails = User.withUsername("admin@melikbakery.com")
                .password("$2a$10$hashedPassword")
                .authorities("ROLE_ADMIN")
                .build();

        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        assertThat(authService.generateToken(userDetails)).isEqualTo("jwt-token");
    }
}
