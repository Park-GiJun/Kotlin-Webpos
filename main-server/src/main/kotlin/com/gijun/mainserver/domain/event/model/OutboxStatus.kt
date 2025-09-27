package com.gijun.mainserver.domain.event.model

enum class OutboxStatus {
    PENDING, PROCESSING, COMPLETED, FAILED
}