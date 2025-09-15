package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.adapter

import com.gijun.mainserver.application.dto.result.organization.pos.PosResult
import com.gijun.mainserver.application.port.out.organization.pos.PosQueryRepository
import com.gijun.mainserver.domain.organization.pos.model.Pos
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.mapper.PosPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.repository.PosJpaRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PosQueryRepositoryAdapter(
    private val posJpaRepository: PosJpaRepository
) : PosQueryRepository {

    // Domain methods (for Command handlers)
    @Transactional(readOnly = true)
    override fun findById(posId: Long): Pos? {
        return posJpaRepository.findById(posId)
            .filter { !it.isDeleted }
            .map { PosPersistenceMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByStoreId(storeId: Long): List<Pos> {
        return posJpaRepository.findByStoreIdAndIsDeletedFalse(storeId)
            .map { PosPersistenceMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByHqId(hqId: Long): List<Pos> {
        return posJpaRepository.findByHqIdAndIsDeletedFalse(hqId)
            .map { PosPersistenceMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findAllActive(): List<Pos> {
        return posJpaRepository.findByIsDeletedFalse()
            .map { PosPersistenceMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun existsByPosNumber(posNumber: String): Boolean {
        return posJpaRepository.existsByPosNumberAndIsDeletedFalse(posNumber)
    }

    @Transactional(readOnly = true)
    override fun existsById(posId: Long): Boolean {
        return posJpaRepository.findById(posId)
            .filter { !it.isDeleted }
            .isPresent
    }

    @Transactional(readOnly = true)
    override fun findByPosNumberContaining(keyword: String): List<Pos> {
        return posJpaRepository.findByPosNumberContainingAndIsDeletedFalse(keyword)
            .map { PosPersistenceMapper.toDomain(it) }
    }

    // Result DTO methods (for Query handlers) - includes audit data from entities
    @Transactional(readOnly = true)
    override fun findPosResultById(posId: Long): PosResult? {
        return posJpaRepository.findById(posId)
            .filter { !it.isDeleted }
            .map { PosPersistenceMapper.toPosResult(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findPosResultsByStoreId(storeId: Long): List<PosResult> {
        return posJpaRepository.findByStoreIdAndIsDeletedFalse(storeId)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @Transactional(readOnly = true)
    override fun findPosResultsByStoreId(storeId: Long, pageable: Pageable): Page<PosResult> {
        return posJpaRepository.findByStoreIdAndIsDeletedFalse(storeId, pageable)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @Transactional(readOnly = true)
    override fun findPosResultsByHqId(hqId: Long): List<PosResult> {
        return posJpaRepository.findByHqIdAndIsDeletedFalse(hqId)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @Transactional(readOnly = true)
    override fun findPosResultsByHqId(hqId: Long, pageable: Pageable): Page<PosResult> {
        return posJpaRepository.findByHqIdAndIsDeletedFalse(hqId, pageable)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @Transactional(readOnly = true)
    override fun findAllPosResults(pageable: Pageable): Page<PosResult> {
        return posJpaRepository.findByIsDeletedFalse(pageable)
            .map { PosPersistenceMapper.toPosResult(it) }
    }

    @Transactional(readOnly = true)
    override fun findPosResultsByNameContaining(keyword: String): List<PosResult> {
        return posJpaRepository.findByPosNumberContainingAndIsDeletedFalse(keyword)
            .map { PosPersistenceMapper.toPosResult(it) }
    }
}