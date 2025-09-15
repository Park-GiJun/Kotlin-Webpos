package com.gijun.posserver.application.dto.result.sales

import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesHeaderResult(
    val id: Long?,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val billNo: String,
    val saleType: Boolean,
    val saleDate: LocalDateTime,
    val saleAmt: BigDecimal,
    val payAmt: BigDecimal,
    val dcAmt: BigDecimal,
    val couponAmt: BigDecimal,
    val cardAmt: BigDecimal,
    val cashAmt: BigDecimal,
    val voucherAmt: BigDecimal,
    val promotionAmt: BigDecimal,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)