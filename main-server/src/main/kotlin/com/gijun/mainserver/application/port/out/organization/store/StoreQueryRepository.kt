package com.gijun.mainserver.application.port.out.organization.store

import com.gijun.mainserver.application.dto.result.organization.store.StoreResult
import com.gijun.mainserver.domain.organization.store.model.Store
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface StoreQueryRepository {
    // Domain methods (for Command handlers)
    fun findById(storeId: Long): Store?
    fun findByHqId(hqId: Long): List<Store>
    fun findAllActive(): List<Store>
    fun existsByName(name: String): Boolean
    fun existsById(storeId: Long): Boolean
    fun findByNameContaining(keyword: String): List<Store>

    // Result DTO methods (for Query handlers) - includes audit data from entities
    fun findStoreResultById(storeId: Long): StoreResult?
    fun findStoreResultsByHqId(hqId: Long): List<StoreResult>
    fun findStoreResultsByHqId(hqId: Long, pageable: Pageable): Page<StoreResult>
    fun findAllStoreResults(pageable: Pageable): Page<StoreResult>
    fun findStoreResultsByNameContaining(keyword: String): List<StoreResult>
}