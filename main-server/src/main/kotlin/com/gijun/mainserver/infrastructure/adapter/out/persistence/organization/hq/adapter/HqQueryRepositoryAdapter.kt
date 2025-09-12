package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.adapter

import com.gijun.mainserver.application.port.out.organization.hq.HqQueryRepository
import com.gijun.mainserver.domain.organization.hq.model.Hq
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.mapper.HqPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.repository.HqJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class HqQueryRepositoryAdapter(
    private val hqJpaRepository: HqJpaRepository
) : HqQueryRepository {

    @Transactional(readOnly = true)
    override fun findById(hqId: Long): Hq? {
        return hqJpaRepository.findById(hqId)
            .filter { !it.isDeleted }
            .map { HqPersistenceMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findAllActive(): List<Hq> {
        return hqJpaRepository.findByIsDeletedFalse()
            .map { HqPersistenceMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun existsByName(name: String): Boolean {
        return hqJpaRepository.existsByNameAndIsDeletedFalse(name)
    }

    @Transactional(readOnly = true)
    override fun existsById(hqId: Long): Boolean {
        return hqJpaRepository.findById(hqId)
            .filter { !it.isDeleted }
            .isPresent
    }

    @Transactional(readOnly = true)
    override fun findByNameContaining(keyword: String): List<Hq> {
        return hqJpaRepository.findByNameContainingAndIsDeletedFalse(keyword)
            .map { HqPersistenceMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findById(hqId: UUID): Hq? {
        // TODO: Implement UUID support when domain model is updated
        return null
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Hq> {
        return hqJpaRepository.findByIsDeletedFalse(pageable)
            .map { HqPersistenceMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun existsById(hqId: UUID): Boolean {
        // TODO: Implement UUID support when domain model is updated
        return false
    }
}