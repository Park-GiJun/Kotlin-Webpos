package com.gijun.posserver.application.dto.command.sales

import java.math.BigDecimal
import java.time.LocalDateTime

data class UpdateSalesDetailCommand(
    val id: Long?,
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
    val dcAmt: BigDecimal = BigDecimal.ZERO,
    val couponAmt: BigDecimal = BigDecimal.ZERO,
    val cardAmt: BigDecimal = BigDecimal.ZERO,
    val cashAmt: BigDecimal = BigDecimal.ZERO,
    val voucherAmt: BigDecimal = BigDecimal.ZERO,
    val promotionAmt: BigDecimal = BigDecimal.ZERO
) {
    init {
        require(lineNo > 0) { "Line number must be positive" }
        require(productId > 0) { "Product ID must be positive" }
        require(productCode.isNotBlank()) { "Product code cannot be blank" }
        require(saleQty > BigDecimal.ZERO) { "Sale quantity must be positive" }
        require(saleAmt >= BigDecimal.ZERO) { "Sale amount cannot be negative" }
        require(payAmt >= BigDecimal.ZERO) { "Pay amount cannot be negative" }
    }
}