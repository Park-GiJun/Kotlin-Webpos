package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto

import java.math.BigDecimal

data class SalesResponse(
    val header: SalesHeaderResponse,
    val details: List<SalesDetailResponse>,
    val payments: List<SalesPaymentResponse>,
    val totalSaleAmount: BigDecimal,
    val totalPaymentAmount: BigDecimal,
    val totalDiscountAmount: BigDecimal,
    val isCompleteTransaction: Boolean
)