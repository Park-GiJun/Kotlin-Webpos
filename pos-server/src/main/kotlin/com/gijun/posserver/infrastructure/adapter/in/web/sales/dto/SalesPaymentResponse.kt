package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesPaymentResponse(
    val id: Long?,
    val billId: Long,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val paymentMethodId: Long,
    val payAmt: BigDecimal,
    val saleType: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val paymentDate: LocalDateTime,

    val changeAmt: BigDecimal,
    val isCompleted: Boolean
)