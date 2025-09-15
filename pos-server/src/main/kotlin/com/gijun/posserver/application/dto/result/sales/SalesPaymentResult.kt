package com.gijun.posserver.application.dto.result.sales

import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesPaymentResult(
    val id: Long?,
    val billId: Long,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val paymentMethodId: Long,
    val payAmt: BigDecimal,
    val saleType: Boolean,
    val paymentDate: LocalDateTime,
    val changeAmt: BigDecimal,
    val isCompleted: Boolean
)