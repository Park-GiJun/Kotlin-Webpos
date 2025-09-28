package com.gijun.posserver.infrastructure.config

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
                    .title("POS Server API")
                    .version("v1.0")
                    .description("Point of Sale System - POS Server API Documentation")
                    .contact(
                        Contact()
                            .name("POS Server Team")
                            .email("pos-server@example.com")
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
                        .url("http://localhost:8081/pos")
                        .description("Local Development Server"),
                    Server()
                        .url("https://api.example.com/pos")
                        .description("Production Server")
                )
            )
    }
}