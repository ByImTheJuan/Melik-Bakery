package com.hyd.pipes_bakery_backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.hyd.pipes_bakery_backend.security.JwtAuthenticatorFilter;
import com.hyd.pipes_bakery_backend.service.ClientUserDetailsService;
import com.hyd.pipes_bakery_backend.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Value("${app.security.csrf.enabled:true}")
    private boolean csrfEnabled;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticatorFilter jwtAuthenticatorFilter) throws Exception {
        
         if (!csrfEnabled) {
            http.csrf(csrf -> csrf.disable());
        } else {

            CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

            http.csrf(csrf -> csrf
                    .csrfTokenRepository(csrfRepository)
                    .requireCsrfProtectionMatcher(new AdminUnsafeRequestMatcher())
            );
        }
        
        return http
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/auth/session").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/order").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/**").hasRole("ADMIN")
                        .requestMatchers("/api/clients", "/api/clients/**").hasRole("ADMIN")
                        .requestMatchers("/api/addresses", "/api/addresses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticatorFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter((request, response, chain) -> {
                    CsrfToken csrfToken =
                            (CsrfToken) request.getAttribute(CsrfToken.class.getName());

                    if (csrfToken != null) {
                        csrfToken.getToken();
                    }

                    chain.doFilter(request, response);

                }, CsrfFilter.class)
                .build();
    }

    @Bean
    public JwtAuthenticatorFilter jwtAuthenticatorFilter(
            JwtService jwtService,
            ClientUserDetailsService userDetailsService
    ) {
        return new JwtAuthenticatorFilter(jwtService, userDetailsService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static class AdminUnsafeRequestMatcher implements RequestMatcher {
        private static final String[] UNSAFE_METHODS = {"POST", "PUT", "PATCH", "DELETE"};
        private static final List<String> ADMIN_PATHS = List.of(
                "/api/auth/logout",
                "/api/products",
                "/api/orders",
                "/api/clients",
                "/api/addresses"
        );

        @Override
        public boolean matches(HttpServletRequest request) {
            if (!List.of(UNSAFE_METHODS).contains(request.getMethod())) {
                return false;
            }

            String requestPath = request.getServletPath();

            return ADMIN_PATHS.stream().anyMatch(path ->
                    requestPath.equals(path) || requestPath.startsWith(path + "/")
            );
        }
    }
}
