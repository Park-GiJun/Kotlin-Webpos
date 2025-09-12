package com.gijun.mainserver.application.handler.query

import com.gijun.mainserver.application.dto.query.store.GetAllStoreQuery
import com.gijun.mainserver.application.dto.query.store.GetStoreByIdQuery
import com.gijun.mainserver.application.dto.query.store.GetStoresByHqIdQuery
import com.gijun.mainserver.application.dto.result.store.StoreResult
import com.gijun.mainserver.application.port.`in`.organziation.store.GetStoreUseCase
import com.gijun.mainserver.application.port.out.organization.store.StoreQueryRepository
import com.gijun.mainserver.infrastructure.config.ReadOnlyTransaction
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class StoreQueryHandler(
    private val storeQueryRepository: StoreQueryRepository
) : GetStoreUseCase {

    @ReadOnlyTransaction
    @Cacheable(value = ["store"], key = "#query.storeId")
    override fun getStoreById(query: GetStoreByIdQuery): StoreResult {
        return storeQueryRepository.findStoreResultById(query.storeId)
            ?: throw NoSuchElementException("Store not found with id: ${query.storeId}")
    }

    @ReadOnlyTransaction
    @Cacheable(
        value = ["store-list"],
        key = "#query.hqId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    )
    override fun getAllStores(query: GetAllStoreQuery, pageable: Pageable): Page<StoreResult> {
        return if (query.hqId != null) {
            storeQueryRepository.findStoreResultsByHqId(query.hqId, pageable)
        } else {
            storeQueryRepository.findAllStoreResults(pageable)
        }
    }

    @ReadOnlyTransaction
    @Cacheable(value = ["store-by-hq"], key = "#query.hqId")
    override fun getStoresByHqId(query: GetStoresByHqIdQuery): List<StoreResult> {
        return storeQueryRepository.findStoreResultsByHqId(query.hqId)
    }

    @ReadOnlyTransaction
    override fun searchStoreByName(name: String): List<StoreResult> {
        return storeQueryRepository.findStoreResultsByNameContaining(name)
    }
}