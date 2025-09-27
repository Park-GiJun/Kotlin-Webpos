package com.gijun.posserver.infrastructure.adapter.out.persistence.event.jpa

import com.gijun.posserver.domain.event.model.OutboxStatus
import com.gijun.posserver.infrastructure.adapter.out.persistence.event.entity.OutboxEventJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxEventJpaRepository : JpaRepository<OutboxEventJpaEntity, Long> {
    fun findByStatusOrderByCreatedAtAsc(status: OutboxStatus, pageable: Pageable): List<OutboxEventJpaEntity>
}