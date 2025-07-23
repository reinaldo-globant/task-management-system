package com.taskmanagement.taskbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagementOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        Contact contact = new Contact()
                .name("Task Management Support")
                .email("support@taskmanagement.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Task Management API")
                .description("RESTful API for managing tasks with different statuses and owners")
                .version("1.0.0")
                .contact(contact)
                .license(mitLicense);
        
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}