package com.gijun.mainserver.application.dto.result.pos

import java.time.Instant

data class PosResult(
    val posId: Long,
    val storeId: Long,
    val hqId: Long,
    val posName: String,
    val location: String,
    val maxOrderCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)