package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.repository

import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.entity.HqJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface HqJpaRepository : JpaRepository<HqJpaEntity, Long> {
    fun findByIsDeletedFalse(): List<HqJpaEntity>
    fun findByIsDeletedFalse(pageable: Pageable): Page<HqJpaEntity>
    fun existsByNameAndIsDeletedFalse(name: String): Boolean
    fun findByNameContainingAndIsDeletedFalse(keyword: String): List<HqJpaEntity>
}