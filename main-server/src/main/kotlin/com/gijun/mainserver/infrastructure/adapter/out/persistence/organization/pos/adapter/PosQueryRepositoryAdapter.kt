package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.adapter

import com.gijun.mainserver.application.dto.result.pos.PosResult
import com.gijun.mainserver.application.port.out.organization.pos.PosQueryRepository
import com.gijun.mainserver.domain.organization.pos.model.Pos
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.mapper.PosPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.repository.PosJpaRepository
import com.gijun.mainserver.infrastructure.config.ReadOnlyTransaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PosQueryRepositoryAdapter(
    private val posJpaRepository: PosJpaRepository
) : PosQueryRepository {

    // Domain methods (for Command handlers)
    @ReadOnlyTransaction
    override fun findById(posId: Long): Pos? {
        return posJpaRepository.findById(posId)
            .filter { !it.isDeleted }
            .map { PosPersistenceMapper.toDomain(it) }
            .orElse(null)
    }

    @ReadOnlyTransaction
    override fun findByStoreId(storeId: Long): List<Pos> {
        return posJpaRepository.findByStoreIdAndIsDeletedFalse(storeId)
            .map { PosPersistenceMapper.toDomain(it) }
    }

    @ReadOnlyTransaction
    override fun findByHqId(hqId: Long): List<Pos> {
        return posJpaRepository.findByHqIdAndIsDeletedFalse(hqId)
            .map { PosPersistenceMapper.toDomain(it) }
    }

    @ReadOnlyTransaction
    override fun findAllActive(): List<Pos> {
        return posJpaRepository.findByIsDeletedFalse()
            .map { PosPersistenceMapper.toDomain(it) }
    }

    @ReadOnlyTransaction
    override fun existsByPosNumber(posNumber: String): Boolean {
        return posJpaRepository.existsByPosNumberAndIsDeletedFalse(posNumber)
    }

    @ReadOnlyTransaction
    override fun existsById(posId: Long): Boolean {
        return posJpaRepository.findById(posId)
            .filter { !it.isDeleted }
            .isPresent
    }

    @ReadOnlyTransaction
    override fun findByPosNumberContaining(keyword: String): List<Pos> {
        return posJpaRepository.findByPosNumberContainingAndIsDeletedFalse(keyword)
            .map { PosPersistenceMapper.toDomain(it) }
    }

    // Result DTO methods (for Query handlers) - includes audit data from entities
    @ReadOnlyTransaction
    override fun findPosResultById(posId: Long): PosResult? {
        return posJpaRepository.findById(posId)
            .filter { !it.isDeleted }
            .map { PosPersistenceMapper.toPosResult(it) }
            .orElse(null)
    }

    @ReadOnlyTransaction
    override fun findPosResultsByStoreId(storeId: Long): List<PosResult> {
        return posJpaRepository.findByStoreIdAndIsDeletedFalse(storeId)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @ReadOnlyTransaction
    override fun findPosResultsByStoreId(storeId: Long, pageable: Pageable): Page<PosResult> {
        return posJpaRepository.findByStoreIdAndIsDeletedFalse(storeId, pageable)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @ReadOnlyTransaction
    override fun findPosResultsByHqId(hqId: Long): List<PosResult> {
        return posJpaRepository.findByHqIdAndIsDeletedFalse(hqId)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @ReadOnlyTransaction
    override fun findPosResultsByHqId(hqId: Long, pageable: Pageable): Page<PosResult> {
        return posJpaRepository.findByHqIdAndIsDeletedFalse(hqId, pageable)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @ReadOnlyTransaction
    override fun findAllPosResults(pageable: Pageable): Page<PosResult> {
        return posJpaRepository.findByIsDeletedFalse(pageable)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @ReadOnlyTransaction
    override fun findPosResultsByNameContaining(keyword: String): List<PosResult> {
        return posJpaRepository.findByPosNumberContainingAndIsDeletedFalse(keyword)
            .map { PosPersistenceMapper.toPosResult(it) }
    }
}