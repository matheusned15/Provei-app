package com.nedware.Backend.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String schemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Provei API")
                        .version("v1")
                        .description("Documentação da API do Provei")
                        .contact(new Contact().name("Time Provei").email("dev@provei.local")))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // Segurança global opcional — os controllers públicos continuam acessíveis;
                // os privados podem reforçar com @SecurityRequirement na classe/métodos.
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}

