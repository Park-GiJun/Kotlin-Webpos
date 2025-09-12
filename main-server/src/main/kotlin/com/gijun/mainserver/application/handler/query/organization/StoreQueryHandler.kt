package com.gijun.mainserver.application.handler.query.organization

import com.gijun.mainserver.application.dto.query.organization.store.GetAllStoreQuery
import com.gijun.mainserver.application.dto.query.organization.store.GetStoreByIdQuery
import com.gijun.mainserver.application.dto.query.organization.store.GetStoresByHqIdQuery
import com.gijun.mainserver.application.dto.result.organization.store.StoreResult
import com.gijun.mainserver.application.port.`in`.organziation.store.GetStoreUseCase
import com.gijun.mainserver.application.port.out.organization.store.StoreQueryRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StoreQueryHandler(
    private val storeQueryRepository: StoreQueryRepository
) : GetStoreUseCase {

    @Transactional(readOnly = true)
    @Cacheable(value = ["store"], key = "#query.storeId")
    override fun getStoreById(query: GetStoreByIdQuery): StoreResult {
        return storeQueryRepository.findStoreResultById(query.storeId)
            ?: throw NoSuchElementException("Store not found with id: ${query.storeId}")
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Cacheable(value = ["store-by-hq"], key = "#query.hqId")
    override fun getStoresByHqId(query: GetStoresByHqIdQuery): List<StoreResult> {
        return storeQueryRepository.findStoreResultsByHqId(query.hqId)
    }

    @Transactional(readOnly = true)
    override fun searchStoreByName(name: String): List<StoreResult> {
        return storeQueryRepository.findStoreResultsByNameContaining(name)
    }
}