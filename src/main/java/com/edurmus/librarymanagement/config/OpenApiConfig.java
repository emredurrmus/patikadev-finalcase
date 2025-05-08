package com.edurmus.librarymanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management API")
                        .description("API for managing library resources")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Emre Durmus")
                                .email("edurrmus@gmail.com")));
    }

}
