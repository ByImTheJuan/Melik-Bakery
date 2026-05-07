package com.hyd.pipes_bakery_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "adminAuthCookie";

    @Bean
    public OpenAPI pipesBakeryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pipes Bakery API")
                        .version("v1")
                        .description("API REST para gestionar productos, carrito, pedidos, clientes, direcciones y autenticacion administrativa de Pipes Bakery.")
                        .contact(new Contact().name("Pipes Bakery")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name("ADMIN_AUTH_TOKEN")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("Cookie HttpOnly Secure creada por /api/auth/login con expiracion de 1 hora.")));
    }
}
