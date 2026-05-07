package com.hyd.pipes_bakery_backend.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.auth.LoginRequestDTO;

@Service
public class AuthService {

    private final ClientUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            ClientUserDetailsService userDetailsService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(LoginRequestDTO request) {
        UserDetails userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (!isAdmin) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return generateToken(userDetails);
    }

    public String generateToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }
}
