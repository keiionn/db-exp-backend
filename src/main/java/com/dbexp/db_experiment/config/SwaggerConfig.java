package com.dbexp.db_experiment.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DB Experiment API")
                        .version("1.0.0")
                        .description("API documentation for the Database Experiment project")
                        .contact(new Contact()
                                .name("uLcdia")
                                .url("https://github.com/uLcdia/db-exp-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/license/mit")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server")));
    }
}