package com.gijun.mainserver.infrastructure.adapter.out.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.mainserver.application.port.out.cache.CachePort
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class RedisCacheAdapter(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) : CachePort {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun <T> get(key: String, clazz: Class<T>): T? {
        return try {
            val value = redisTemplate.opsForValue().get(key)
            if (value != null) {
                logger.debug("Cache HIT for key: $key")
                objectMapper.convertValue(value, clazz)
            } else {
                logger.debug("Cache MISS for key: $key")
                null
            }
        } catch (e: Exception) {
            logger.error("Error getting cache for key: $key", e)
            null
        }
    }

    override fun set(key: String, value: Any, ttl: Duration) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl)
            logger.debug("Cache SET for key: $key with TTL: ${ttl.toSeconds()}s")
        } catch (e: Exception) {
            logger.error("Error setting cache for key: $key", e)
        }
    }

    override fun delete(key: String): Boolean {
        return try {
            val deleted = redisTemplate.delete(key)
            logger.debug("Cache DELETE for key: $key, result: $deleted")
            deleted
        } catch (e: Exception) {
            logger.error("Error deleting cache for key: $key", e)
            false
        }
    }

    override fun deletePattern(pattern: String): Long {
        return try {
            val keys = redisTemplate.keys(pattern)
            if (keys.isNotEmpty()) {
                val count = redisTemplate.delete(keys)
                logger.debug("Cache DELETE pattern: $pattern, deleted: $count keys")
                count
            } else {
                0
            }
        } catch (e: Exception) {
            logger.error("Error deleting cache pattern: $pattern", e)
            0
        }
    }

    override fun exists(key: String): Boolean {
        return try {
            redisTemplate.hasKey(key)
        } catch (e: Exception) {
            logger.error("Error checking cache existence for key: $key", e)
            false
        }
    }

    override fun <T> getOrCompute(
        key: String,
        ttl: Duration,
        clazz: Class<T>,
        compute: () -> T?
    ): T? {
        val cached = get(key, clazz)
        if (cached != null) {
            return cached
        }

        val computed = compute()
        if (computed != null) {
            set(key, computed, ttl)
        }
        return computed
    }
}