package com.gijun.posserver.infrastructure.adapter.out.persistence.event.mapper

import com.gijun.posserver.domain.event.model.OutboxEvent
import com.gijun.posserver.infrastructure.adapter.out.persistence.event.entity.OutboxEventJpaEntity

object OutboxEventMapper {

    fun toJpaEntity(domain: OutboxEvent): OutboxEventJpaEntity {
        return OutboxEventJpaEntity(
            id = domain.id ?: 0L,
            eventId = domain.eventId,
            eventType = domain.eventType,
            aggregateType = domain.aggregateType,
            aggregateId = domain.aggregateId,
            eventData = domain.eventData,
            createdAt = domain.createdAt,
            processedAt = domain.processedAt,
            status = domain.status,
            retryCount = domain.retryCount
        )
    }

    fun toDomain(entity: OutboxEventJpaEntity): OutboxEvent {
        return OutboxEvent(
            id = entity.id,
            eventId = entity.eventId,
            eventType = entity.eventType,
            aggregateType = entity.aggregateType,
            aggregateId = entity.aggregateId,
            eventData = entity.eventData,
            createdAt = entity.createdAt,
            processedAt = entity.processedAt,
            status = entity.status,
            retryCount = entity.retryCount
        )
    }
}