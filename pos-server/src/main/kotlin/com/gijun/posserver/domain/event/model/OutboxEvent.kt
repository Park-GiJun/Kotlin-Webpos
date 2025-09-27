package com.gijun.posserver.domain.event.model

import java.time.LocalDateTime

data class OutboxEvent(
    val id: Long?,
    val eventId: String,
    val eventType: String,
    val aggregateType: String,
    val aggregateId: String,
    val eventData: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val processedAt: LocalDateTime? = null,
    val status: OutboxStatus,
    val retryCount: Int = 0
) {
    fun markAsProcessing(): OutboxEvent = this.copy(status = OutboxStatus.PROCESSING)
    fun markAsCompleted(): OutboxEvent = this.copy(status = OutboxStatus.COMPLETED, processedAt = LocalDateTime.now())
    fun markAsFailed(): OutboxEvent = this.copy(status = OutboxStatus.FAILED, retryCount = retryCount + 1)
}