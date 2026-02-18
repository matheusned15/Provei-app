package com.nedware.Backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI proveiOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Provei API")
                        .description("Backend API for Provei Application")
                        .version("1.0")
                        .contact(new Contact().name("Provei Team").email("contact@provei.com")));
    }
}
