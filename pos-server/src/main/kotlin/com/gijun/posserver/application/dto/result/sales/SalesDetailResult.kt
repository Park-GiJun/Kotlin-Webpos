package com.gijun.posserver.application.dto.result.sales

import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesDetailResult(
    val id: Long?,
    val billId: Long,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val lineNo: Int,
    val productId: Long,
    val productCode: String,
    val qty: BigDecimal,
    val unitAmt: BigDecimal,
    val saleQty: BigDecimal,
    val saleType: Boolean,
    val saleDate: LocalDateTime,
    val saleAmt: BigDecimal,
    val payAmt: BigDecimal,
    val dcAmt: BigDecimal,
    val couponAmt: BigDecimal,
    val cardAmt: BigDecimal,
    val cashAmt: BigDecimal,
    val voucherAmt: BigDecimal,
    val promotionAmt: BigDecimal
)