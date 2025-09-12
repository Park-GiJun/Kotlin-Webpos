package com.gijun.mainserver.application.dto.result.store

import java.time.Instant
import java.util.UUID

data class StoreResult(
    val storeId: Long,
    val hqId: Long,
    val storeName: String,
    val managerName: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val email: String,
    val phoneNumber: String,
    val businessNumber: String,
    val createdAt: Instant,
    val updatedAt: Instant
)