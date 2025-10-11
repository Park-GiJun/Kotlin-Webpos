package com.gijun.posserver.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("POS Server API")
                    .description("Point of Sale Server REST API Documentation")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("POS Team")
                            .email("pos@example.com")
                    )
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8081/pos")
                        .description("Local Development Server")
                )
            )
    }
}
