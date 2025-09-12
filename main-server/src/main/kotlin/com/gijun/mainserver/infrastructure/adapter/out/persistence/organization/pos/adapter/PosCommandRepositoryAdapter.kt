package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.adapter

import com.gijun.mainserver.application.port.out.organization.pos.PosCommandRepository
import com.gijun.mainserver.domain.organization.pos.model.Pos
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.mapper.PosPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.repository.PosJpaRepository
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.repository.StoreJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PosCommandRepositoryAdapter(
    private val posJpaRepository: PosJpaRepository,
    private val storeJpaRepository: StoreJpaRepository
) : PosCommandRepository {

    @Transactional
    override fun save(pos: Pos, requestId: String): Pos {
        // Get HQ ID from Store
        val store = storeJpaRepository.findById(pos.storeId)
            .orElseThrow { NoSuchElementException("Store not found with id: ${pos.storeId}") }

        val entity = PosPersistenceMapper.toEntity(pos, store.hqId)
        entity.initializeAudit(requestId)
        val savedEntity = posJpaRepository.save(entity)
        return PosPersistenceMapper.toDomain(savedEntity)
    }

    @Transactional
    override fun update(pos: Pos, requestId: String): Pos {
        val existingEntity = posJpaRepository.findById(pos.id!!)
            .orElseThrow { NoSuchElementException("POS not found with id: ${pos.id}") }

        // Get HQ ID from Store
        val store = storeJpaRepository.findById(pos.storeId)
            .orElseThrow { NoSuchElementException("Store not found with id: ${pos.storeId}") }

        val updatedEntity = PosPersistenceMapper.updateEntity(existingEntity, pos, store.hqId)
        updatedEntity.updateAudit(requestId)
        val savedEntity = posJpaRepository.save(updatedEntity)
        return PosPersistenceMapper.toDomain(savedEntity)
    }

    @Transactional
    override fun deleteById(posId: Long, requestId: String) {
        val entity = posJpaRepository.findById(posId)
            .orElseThrow { NoSuchElementException("POS not found with id: $posId") }
        entity.markAsDeleted(requestId)
        posJpaRepository.save(entity)
    }
}