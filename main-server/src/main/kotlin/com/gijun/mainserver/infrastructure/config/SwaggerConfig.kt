package com.gijun.mainserver.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Main Server API")
                    .version("v1.0")
                    .description("Multi-Store Management System - Main Server API Documentation")
                    .contact(
                        Contact()
                            .name("Main Server Team")
                            .email("main-server@example.com")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                    )
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8080/main")
                        .description("Local Development Server"),
                    Server()
                        .url("https://api.example.com/main")
                        .description("Production Server")
                )
            )
    }
}