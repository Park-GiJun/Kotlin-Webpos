package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.repository

import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.entity.PosJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PosJpaRepository : JpaRepository<PosJpaEntity, Long> {
    fun findByStoreIdAndIsDeletedFalse(storeId: Long): List<PosJpaEntity>
    fun findByStoreIdAndIsDeletedFalse(storeId: Long, pageable: Pageable): Page<PosJpaEntity>
    fun findByHqIdAndIsDeletedFalse(hqId: Long): List<PosJpaEntity>
    fun findByHqIdAndIsDeletedFalse(hqId: Long, pageable: Pageable): Page<PosJpaEntity>
    fun findByIsDeletedFalse(): List<PosJpaEntity>
    fun findByIsDeletedFalse(pageable: Pageable): Page<PosJpaEntity>
    fun existsByPosNumberAndIsDeletedFalse(posNumber: String): Boolean
    fun findByPosNumberContainingAndIsDeletedFalse(keyword: String): List<PosJpaEntity>
}