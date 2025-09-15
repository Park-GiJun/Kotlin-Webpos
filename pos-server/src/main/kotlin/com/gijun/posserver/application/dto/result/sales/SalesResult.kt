package com.gijun.posserver.application.dto.result.sales

import java.math.BigDecimal

data class SalesResult(
    val header: SalesHeaderResult,
    val details: List<SalesDetailResult>,
    val payments: List<SalesPaymentResult>,
    val totalSaleAmount: BigDecimal,
    val totalPaymentAmount: BigDecimal,
    val totalDiscountAmount: BigDecimal,
    val isCompleteTransaction: Boolean
)