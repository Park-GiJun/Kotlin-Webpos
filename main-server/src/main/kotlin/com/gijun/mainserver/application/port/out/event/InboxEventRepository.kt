package com.gijun.mainserver.application.port.out.event

import com.gijun.mainserver.domain.event.model.InboxEvent
import com.gijun.mainserver.domain.event.model.InboxStatus
import org.springframework.data.domain.Pageable

interface InboxEventRepository {
    fun save(inboxEvent: InboxEvent): InboxEvent
    fun existsByEventId(eventId: String): Boolean
    fun findByStatusOrderByCreatedAtAsc(status: InboxStatus, pageable: Pageable): List<InboxEvent>
}