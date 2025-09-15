package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class UpdateSalesResponse(
    val id: Long?,
    val billNo: String,
    val totalSaleAmount: BigDecimal,
    val totalPaymentAmount: BigDecimal,
    val isCompleteTransaction: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime?
)