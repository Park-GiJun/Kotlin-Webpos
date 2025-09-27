package com.gijun.mainserver.infrastructure.adapter.out.persistence.event.mapper

import com.gijun.mainserver.domain.event.model.InboxEvent
import com.gijun.mainserver.infrastructure.adapter.out.persistence.event.entity.InboxEventJpaEntity

object InboxEventMapper {

    fun toJpaEntity(domain: InboxEvent): InboxEventJpaEntity {
        return InboxEventJpaEntity(
            id = domain.id ?: 0L,
            eventId = domain.eventId,
            eventType = domain.eventType,
            aggregateType = domain.aggregateType,
            aggregateId = domain.aggregateId,
            eventData = domain.eventData,
            status = com.gijun.mainserver.infrastructure.adapter.out.persistence.event.entity.InboxStatus.valueOf(domain.status.name),
            retryCount = domain.retryCount
        )
    }

    fun toDomain(entity: InboxEventJpaEntity): InboxEvent {
        return InboxEvent(
            id = entity.id,
            eventId = entity.eventId,
            eventType = entity.eventType,
            aggregateType = entity.aggregateType,
            aggregateId = entity.aggregateId,
            eventData = entity.eventData,
            status = com.gijun.mainserver.domain.event.model.InboxStatus.valueOf(entity.status.name),
            retryCount = entity.retryCount
        )
    }
}