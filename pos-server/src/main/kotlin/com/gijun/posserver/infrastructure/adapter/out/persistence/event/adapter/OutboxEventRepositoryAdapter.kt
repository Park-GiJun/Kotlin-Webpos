package com.gijun.posserver.infrastructure.adapter.out.persistence.event.adapter

import com.gijun.posserver.application.port.out.event.OutboxEventRepository
import com.gijun.posserver.domain.event.model.OutboxEvent
import com.gijun.posserver.domain.event.model.OutboxStatus
import com.gijun.posserver.infrastructure.adapter.out.persistence.event.jpa.OutboxEventJpaRepository
import com.gijun.posserver.infrastructure.adapter.out.persistence.event.mapper.OutboxEventMapper
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class OutboxEventRepositoryAdapter(
    private val outboxEventJpaRepository: OutboxEventJpaRepository
) : OutboxEventRepository {

    override fun save(outboxEvent: OutboxEvent): OutboxEvent {
        val jpaEntity = OutboxEventMapper.toJpaEntity(outboxEvent)
        val savedEntity = outboxEventJpaRepository.save(jpaEntity)
        return OutboxEventMapper.toDomain(savedEntity)
    }

    override fun findByStatusOrderByCreatedAtAsc(
        status: OutboxStatus,
        pageable: Pageable
    ): List<OutboxEvent> {
        return outboxEventJpaRepository.findByStatusOrderByCreatedAtAsc(status, pageable)
            .map { OutboxEventMapper.toDomain(it) }
    }
}