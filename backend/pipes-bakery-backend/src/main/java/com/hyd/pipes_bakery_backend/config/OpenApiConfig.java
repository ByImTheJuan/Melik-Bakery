package com.hyd.pipes_bakery_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@Profile("!prod")
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "adminAuthCookie";

    @Bean
    public OpenAPI pipesBakeryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Melik Bakery API")
                        .version("v1")
                        .description("API REST para gestionar productos, carrito, pedidos, clientes, direcciones y autenticacion administrativa de Melik Bakery.")
                        .contact(new Contact().name("Melik Bakery")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name("ADMIN_AUTH_TOKEN")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("Cookie HttpOnly Secure creada por /api/auth/login con expiracion de 1 hora.")));
    }
}
