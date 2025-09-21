package com.gijun.mainserver.application.port.out.cache

import java.time.Duration

interface CachePort {
    fun <T> get(key: String, clazz: Class<T>): T?
    fun set(key: String, value: Any, ttl: Duration = Duration.ofMinutes(5))
    fun delete(key: String): Boolean
    fun deletePattern(pattern: String): Long
    fun exists(key: String): Boolean
    fun <T> getOrCompute(
        key: String,
        ttl: Duration = Duration.ofMinutes(5),
        clazz: Class<T>,
        compute: () -> T?
    ): T?
}