package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.adapter

import com.gijun.mainserver.application.dto.result.store.StoreResult
import com.gijun.mainserver.application.port.out.organization.store.StoreQueryRepository
import com.gijun.mainserver.domain.organization.store.model.Store
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.mapper.StorePersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.repository.StoreJpaRepository
import com.gijun.mainserver.infrastructure.config.ReadOnlyTransaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class StoreQueryRepositoryAdapter(
    private val storeJpaRepository: StoreJpaRepository
) : StoreQueryRepository {

    // Domain methods (for Command handlers)
    @ReadOnlyTransaction
    override fun findById(storeId: Long): Store? {
        return storeJpaRepository.findById(storeId)
            .filter { !it.isDeleted }
            .map { StorePersistenceMapper.toDomain(it) }
            .orElse(null)
    }

    @ReadOnlyTransaction
    override fun findByHqId(hqId: Long): List<Store> {
        return storeJpaRepository.findByHqIdAndIsDeletedFalse(hqId)
            .map { StorePersistenceMapper.toDomain(it) }
    }

    @ReadOnlyTransaction
    override fun findAllActive(): List<Store> {
        return storeJpaRepository.findByIsDeletedFalse()
            .map { StorePersistenceMapper.toDomain(it) }
    }

    @ReadOnlyTransaction
    override fun existsByName(name: String): Boolean {
        return storeJpaRepository.existsByNameAndIsDeletedFalse(name)
    }

    @ReadOnlyTransaction
    override fun existsById(storeId: Long): Boolean {
        return storeJpaRepository.findById(storeId)
            .filter { !it.isDeleted }
            .isPresent
    }

    @ReadOnlyTransaction
    override fun findByNameContaining(keyword: String): List<Store> {
        return storeJpaRepository.findByNameContainingAndIsDeletedFalse(keyword)
            .map { StorePersistenceMapper.toDomain(it) }
    }

    // Result DTO methods (for Query handlers) - includes audit data from entities
    @ReadOnlyTransaction
    override fun findStoreResultById(storeId: Long): StoreResult? {
        return storeJpaRepository.findById(storeId)
            .filter { !it.isDeleted }
            .map { StorePersistenceMapper.toStoreResult(it) }
            .orElse(null)
    }

    @ReadOnlyTransaction
    override fun findStoreResultsByHqId(hqId: Long): List<StoreResult> {
        return storeJpaRepository.findByHqIdAndIsDeletedFalse(hqId)
            .map { StorePersistenceMapper.toStoreResult(it) }
    }

    @ReadOnlyTransaction
    override fun findStoreResultsByHqId(hqId: Long, pageable: Pageable): Page<StoreResult> {
        return storeJpaRepository.findByHqIdAndIsDeletedFalse(hqId, pageable)
            .map { StorePersistenceMapper.toStoreResult(it) }
    }

    @ReadOnlyTransaction
    override fun findAllStoreResults(pageable: Pageable): Page<StoreResult> {
        return storeJpaRepository.findByIsDeletedFalse(pageable)
            .map { StorePersistenceMapper.toStoreResult(it) }
    }

    @ReadOnlyTransaction
    override fun findStoreResultsByNameContaining(keyword: String): List<StoreResult> {
        return storeJpaRepository.findByNameContainingAndIsDeletedFalse(keyword)
            .map { StorePersistenceMapper.toStoreResult(it) }
    }
}