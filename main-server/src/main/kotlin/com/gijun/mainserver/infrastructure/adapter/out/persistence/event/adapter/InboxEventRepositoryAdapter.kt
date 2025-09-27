package com.gijun.mainserver.infrastructure.adapter.out.persistence.event.adapter

import com.gijun.mainserver.application.port.out.event.InboxEventRepository
import com.gijun.mainserver.domain.event.model.InboxEvent
import com.gijun.mainserver.domain.event.model.InboxStatus
import com.gijun.mainserver.infrastructure.adapter.out.persistence.event.jpa.InboxEventJpaRepository
import com.gijun.mainserver.infrastructure.adapter.out.persistence.event.mapper.InboxEventMapper
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class InboxEventRepositoryAdapter(
    private val inboxEventJpaRepository: InboxEventJpaRepository
) : InboxEventRepository {

    override fun save(inboxEvent: InboxEvent): InboxEvent {
        val jpaEntity = InboxEventMapper.toJpaEntity(inboxEvent)
        val savedEntity = inboxEventJpaRepository.save(jpaEntity)
        return InboxEventMapper.toDomain(savedEntity)
    }

    override fun existsByEventId(eventId: String): Boolean {
        return inboxEventJpaRepository.existsByEventId(eventId)
    }

    override fun findByStatusOrderByCreatedAtAsc(status: InboxStatus, pageable: Pageable): List<InboxEvent> {
        val jpaStatus = com.gijun.mainserver.infrastructure.adapter.out.persistence.event.entity.InboxStatus.valueOf(status.name)
        return inboxEventJpaRepository.findByStatusOrderByCreatedAtAsc(jpaStatus, pageable)
            .map { InboxEventMapper.toDomain(it) }
    }
}