package com.gijun.posserver.application.dto.result.sales

import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesResult(
    val header: SalesHeaderResult,
    val details: List<SalesDetailResult>,
    val payments: List<SalesPaymentResult>,
    val totalSaleAmount: BigDecimal,
    val totalPaymentAmount: BigDecimal,
    val totalDiscountAmount: BigDecimal,
    val isCompleteTransaction: Boolean
)

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