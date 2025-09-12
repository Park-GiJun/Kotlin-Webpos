package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.repository

import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.entity.StoreJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface StoreJpaRepository : JpaRepository<StoreJpaEntity, Long> {
    fun findByHqIdAndIsDeletedFalse(hqId: Long): List<StoreJpaEntity>
    fun findByHqIdAndIsDeletedFalse(hqId: Long, pageable: Pageable): Page<StoreJpaEntity>
    fun findByIsDeletedFalse(): List<StoreJpaEntity>
    fun findByIsDeletedFalse(pageable: Pageable): Page<StoreJpaEntity>
    fun existsByNameAndIsDeletedFalse(name: String): Boolean
    fun findByNameContainingAndIsDeletedFalse(keyword: String): List<StoreJpaEntity>
}