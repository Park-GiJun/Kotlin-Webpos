package com.gijun.mainserver.application.dto.result.hq

import java.time.Instant
import java.util.UUID

data class HqResult(
    val hqId: UUID,
    val name: String,
    val representation: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val email: String,
    val phoneNumber: String,
    val createdAt: Instant,
    val updatedAt: Instant
)