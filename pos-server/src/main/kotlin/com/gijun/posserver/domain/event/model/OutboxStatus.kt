package com.gijun.posserver.domain.event.model

enum class OutboxStatus {
    PENDING, PROCESSING, COMPLETED, FAILED
}