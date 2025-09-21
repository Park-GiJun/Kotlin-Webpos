package com.gijun.mainserver.application.handler.cache

import com.gijun.mainserver.application.port.out.cache.CachePort
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class CacheHandler(
    private val cachePort: CachePort
) {

    fun <T> get(key: String, clazz: Class<T>): T? {
        return cachePort.get(key, clazz)
    }

    fun set(key: String, value: Any, ttl: Duration = Duration.ofMinutes(5)) {
        cachePort.set(key, value, ttl)
    }

    fun delete(key: String): Boolean {
        return cachePort.delete(key)
    }

    fun deletePattern(pattern: String): Long {
        return cachePort.deletePattern(pattern)
    }

    fun exists(key: String): Boolean {
        return cachePort.exists(key)
    }

    fun <T> getOrCompute(
        key: String,
        ttl: Duration = Duration.ofMinutes(5),
        clazz: Class<T>,
        compute: () -> T?
    ): T? {
        return cachePort.getOrCompute(key, ttl, clazz, compute)
    }

    object Keys {
        const val PRODUCT_PREFIX = "product"
        const val PRODUCT_LIST_PREFIX = "product:list"
        const val PRODUCT_CONTAINER_PREFIX = "product:container"
        const val PRODUCT_CONTAINER_LIST_PREFIX = "product:container:list"

        fun productKey(productId: Long): String = "$PRODUCT_PREFIX:$productId"
        fun productListByHqKey(hqId: Long): String = "$PRODUCT_LIST_PREFIX:hq:$hqId"
        fun productListByStoreKey(storeId: Long): String = "$PRODUCT_LIST_PREFIX:store:$storeId"
        fun productListByContainerKey(containerId: Long): String = "$PRODUCT_LIST_PREFIX:container:$containerId"
        fun allProductsKey(): String = "$PRODUCT_LIST_PREFIX:all"

        fun productContainerKey(hqId: Long, containerId: Long): String =
            "$PRODUCT_CONTAINER_PREFIX:hq:$hqId:container:$containerId"
        fun productContainerListByHqKey(hqId: Long): String = "$PRODUCT_CONTAINER_LIST_PREFIX:hq:$hqId"
    }
}