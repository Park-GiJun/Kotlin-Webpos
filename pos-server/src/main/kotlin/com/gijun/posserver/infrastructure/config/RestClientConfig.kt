package com.gijun.posserver.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Value("\${main-server.base-url}")
    private lateinit var mainServerBaseUrl : String

    @Bean
    fun restClient(): RestClient {
        val requestFactory = JdkClientHttpRequestFactory()
        requestFactory.setReadTimeout(java.time.Duration.ofSeconds(5))
        return RestClient.builder()
            .baseUrl(mainServerBaseUrl)
            .requestFactory(requestFactory)
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}