package com.gijun.mainserver.infrastructure.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.spring.cache.CacheConfig
import org.redisson.spring.cache.RedissonSpringCacheManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableCaching
class RedissonConfig {

    @Value("\${spring.redis.host:localhost}")
    private lateinit var redisHost: String

    @Value("\${spring.redis.port:6379}")
    private var redisPort: Int = 6379

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://$redisHost:$redisPort")
            .setConnectionPoolSize(10)
            .setConnectionMinimumIdleSize(5)
            .setTimeout(3000)
            .setRetryAttempts(3)
            .setRetryInterval(1500)

        return Redisson.create(config)
    }

    @Bean
    fun cacheManager(redissonClient: RedissonClient): CacheManager {
        val config = mutableMapOf<String, CacheConfig>()

        config["hq"] = CacheConfig(
            10 * 60 * 1000,
            5 * 60 * 1000
        )

        config["hq-list"] = CacheConfig(
            5 * 60 * 1000,
            2 * 60 * 1000
        )

        config["store"] = CacheConfig(
            10 * 60 * 1000,
            5 * 60 * 1000
        )

        config["pos"] = CacheConfig(
            10 * 60 * 1000,
            5 * 60 * 1000
        )

        config["product"] = CacheConfig(
            30 * 60 * 1000,
            15 * 60 * 1000
        )

        return RedissonSpringCacheManager(redissonClient, config)
    }
}