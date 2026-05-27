package com.hyd.pipes_bakery_backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.auth.LoginRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.model.Product;
import com.hyd.pipes_bakery_backend.model.Role;
import com.hyd.pipes_bakery_backend.repository.ClientRepository;
import com.hyd.pipes_bakery_backend.repository.ProductRepository;

import jakarta.servlet.http.Cookie;

@SpringBootTest(properties = {
        "app.security.csrf.enabled=true"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminSecurityFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        Client admin = new Client();
        admin.setFirstName("Admin");
        admin.setLastName("Bakery");
        admin.setEmail("admin@melikbakery.com");
        admin.setPassword(passwordEncoder.encode("safe-password"));
        admin.setRole(Role.ADMIN);
        clientRepository.save(admin);
    }

    @Test
    void shouldRejectLoginWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "CSRF token is missing or invalid. Refresh the page and try again."
                ));
    }

    @Test
    void shouldReportAnonymousSessionStatusWithoutAnAuthorizationError() throws Exception {
        mockMvc.perform(get("/api/auth/session-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void shouldAuthenticateUpdateProductAndRejectReusedJwtAfterLogout() throws Exception {
        Cookie preLoginCsrf = fetchCsrfToken(null);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .cookie(preLoginCsrf)
                        .header("X-XSRF-TOKEN", preLoginCsrf.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andReturn();

        Cookie authCookie = loginResult.getResponse().getCookie("ADMIN_AUTH_TOKEN");
        assertThat(authCookie).isNotNull();
        Cookie adminCsrf = fetchCsrfToken(authCookie);

        mockMvc.perform(get("/api/auth/session").cookie(authCookie, adminCsrf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true));

        mockMvc.perform(get("/api/auth/session-status").cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true));

        Product product = new Product("Croissant", new BigDecimal("9500"), "Original",
                List.of("Harina"), "/images/products/croissant.jpg");
        Product savedProduct = productRepository.save(product);

        ProductRequestDTO update = new ProductRequestDTO();
        update.setName("Croissant premium");
        update.setDescription("Actualizado");
        update.setPrice(new BigDecimal("10500"));
        update.setIngredients(List.of("Harina", "Mantequilla"));
        update.setImageUrl("/images/products/croissant-premium.jpg");

        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                        .cookie(authCookie, adminCsrf)
                        .header("X-XSRF-TOKEN", adminCsrf.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Croissant premium"))
                .andExpect(jsonPath("$.description").value("Actualizado"));

        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "CSRF token is missing or invalid. Refresh the page and try again."
                ));

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(authCookie, adminCsrf)
                        .header("X-XSRF-TOKEN", adminCsrf.getValue()))
                .andExpect(status().isNoContent());

        verify(valueOperations).set(anyString(), eq("revoked"), any(Duration.class));
        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        Cookie anonymousCsrf = fetchCsrfToken(null);

        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                        .cookie(authCookie, anonymousCsrf)
                        .header("X-XSRF-TOKEN", anonymousCsrf.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication is required."));
    }

    private Cookie fetchCsrfToken(Cookie authCookie) throws Exception {
        var request = get("/api/auth/csrf");
        if (authCookie != null) {
            request.cookie(authCookie);
        }

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerName").value("X-XSRF-TOKEN"))
                .andReturn();
        return result.getResponse().getCookie("XSRF-TOKEN");
    }

    private LoginRequestDTO loginRequest() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("admin@melikbakery.com");
        request.setPassword("safe-password");
        return request;
    }
}
