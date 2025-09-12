package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.adapter

import com.gijun.mainserver.application.port.out.organization.hq.HqCommandRepository
import com.gijun.mainserver.domain.organization.hq.model.Hq
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.mapper.HqPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.repository.HqJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class HqCommandRepositoryAdapter(
    private val hqJpaRepository: HqJpaRepository
) : HqCommandRepository {

    @Transactional
    override fun save(hq: Hq, requestId: String): Hq {
        val entity = HqPersistenceMapper.toEntity(hq)
        entity.initializeAudit(requestId)
        val savedEntity = hqJpaRepository.save(entity)
        return HqPersistenceMapper.toDomain(savedEntity)
    }

    @Transactional
    override fun update(hq: Hq, requestId: String): Hq {
        val existingEntity = hqJpaRepository.findById(hq.id!!)
            .orElseThrow { NoSuchElementException("HQ not found with id: ${hq.id}") }

        val updatedEntity = HqPersistenceMapper.updateEntity(existingEntity, hq)
        updatedEntity.updateAudit(requestId)
        val savedEntity = hqJpaRepository.save(updatedEntity)
        return HqPersistenceMapper.toDomain(savedEntity)
    }

    @Transactional
    override fun deleteById(hqId: Long, requestId: String) {
        val entity = hqJpaRepository.findById(hqId)
            .orElseThrow { NoSuchElementException("HQ not found with id: $hqId") }
        entity.markAsDeleted(requestId)
        hqJpaRepository.save(entity)
    }
}