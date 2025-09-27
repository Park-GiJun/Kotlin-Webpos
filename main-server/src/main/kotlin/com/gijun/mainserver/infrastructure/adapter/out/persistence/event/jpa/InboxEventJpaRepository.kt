package com.gijun.mainserver.infrastructure.adapter.out.persistence.event.jpa

import com.gijun.mainserver.infrastructure.adapter.out.persistence.event.entity.InboxEventJpaEntity
import com.gijun.mainserver.infrastructure.adapter.out.persistence.event.entity.InboxStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface InboxEventJpaRepository : JpaRepository<InboxEventJpaEntity, Long> {
    fun existsByEventId(eventId: String): Boolean
    fun findByStatusOrderByCreatedAtAsc(status: InboxStatus, pageable: Pageable): List<InboxEventJpaEntity>
}