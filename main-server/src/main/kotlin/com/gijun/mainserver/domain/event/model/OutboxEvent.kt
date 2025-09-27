package com.gijun.mainserver.domain.event.model

data class OutboxEvent(
    val id: Long?,
    val eventId: String,
    val eventType: String,
    val aggregateType: String,
    val aggregateId: String,
    val eventData: String,
    val status: OutboxStatus,
    val retryCount: Int = 0
) {
    fun markAsProcessing(): OutboxEvent = this.copy(status = OutboxStatus.PROCESSING)
    fun markAsCompleted(): OutboxEvent = this.copy(status = OutboxStatus.COMPLETED)
    fun markAsFailed(): OutboxEvent = this.copy(status = OutboxStatus.FAILED, retryCount = retryCount + 1)
}