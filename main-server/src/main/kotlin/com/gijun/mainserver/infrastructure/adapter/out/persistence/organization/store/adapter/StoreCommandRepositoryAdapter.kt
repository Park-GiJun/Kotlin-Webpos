package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.adapter

import com.gijun.mainserver.application.port.out.organization.store.StoreCommandRepository
import com.gijun.mainserver.domain.organization.store.model.Store
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.mapper.StorePersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.repository.StoreJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StoreCommandRepositoryAdapter(
    private val storeJpaRepository: StoreJpaRepository
) : StoreCommandRepository {

    @Transactional
    override fun save(store: Store, requestId: String): Store {
        val entity = StorePersistenceMapper.toEntity(store)
        entity.initializeAudit(requestId)
        val savedEntity = storeJpaRepository.save(entity)
        return StorePersistenceMapper.toDomain(savedEntity)
    }

    @Transactional
    override fun update(store: Store, requestId: String): Store {
        val existingEntity = storeJpaRepository.findById(store.id!!)
            .orElseThrow { NoSuchElementException("Store not found with id: ${store.id}") }

        val updatedEntity = StorePersistenceMapper.updateEntity(existingEntity, store)
        updatedEntity.updateAudit(requestId)
        val savedEntity = storeJpaRepository.save(updatedEntity)
        return StorePersistenceMapper.toDomain(savedEntity)
    }

    @Transactional
    override fun deleteById(storeId: Long, requestId: String) {
        val entity = storeJpaRepository.findById(storeId)
            .orElseThrow { NoSuchElementException("Store not found with id: $storeId") }
        entity.markAsDeleted(requestId)
        storeJpaRepository.save(entity)
    }
}