package com.gijun.mainserver.domain.event.model

data class InboxEvent(
    val id: Long?,
    val eventId: String,
    val eventType: String,
    val aggregateType: String,
    val aggregateId: String,
    val eventData: String,
    val status: InboxStatus,
    val retryCount: Int = 0
) {
    fun markAsProcessing(): InboxEvent = this.copy(status = InboxStatus.PROCESSING)
    fun markAsCompleted(): InboxEvent = this.copy(status = InboxStatus.COMPLETED)
    fun markAsFailed(): InboxEvent = this.copy(status = InboxStatus.FAILED, retryCount = retryCount + 1)
}