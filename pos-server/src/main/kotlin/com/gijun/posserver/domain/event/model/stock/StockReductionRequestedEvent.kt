package com.gijun.posserver.domain.event.model.stock

import java.math.BigDecimal
import java.time.LocalDateTime

data class StockReductionRequestedEvent(
    val eventId: String,
    val salesId: String,
    val storeId: Long,
    val posId: Long,
    val soldItems: List<SoldItem>,
    val salesDate: LocalDateTime
)