package com.gijun.posserver.application.port.out.event

import com.gijun.posserver.domain.event.model.OutboxEvent
import com.gijun.posserver.domain.event.model.OutboxStatus
import org.springframework.data.domain.Pageable

interface OutboxEventRepository {
    fun save(outboxEvent: OutboxEvent): OutboxEvent
    fun findByStatusOrderByCreatedAtAsc(status: OutboxStatus, pageable: Pageable): List<OutboxEvent>
}