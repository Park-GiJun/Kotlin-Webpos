package com.gijun.posserver.application.dto.command.sales

import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateSalesCommand(
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val billNo: String,
    val saleType: Boolean,
    val saleDate: LocalDateTime,
    val details: List<CreateSalesDetailCommand>,
    val payments: List<CreateSalesPaymentCommand>
) {
    init {
        require(hqId > 0) { "HQ ID must be positive" }
        require(storeId > 0) { "Store ID must be positive" }
        require(posId > 0) { "POS ID must be positive" }
        require(billNo.isNotBlank()) { "Bill number cannot be blank" }
        require(details.isNotEmpty()) { "Sales must have at least one detail item" }
        require(payments.isNotEmpty()) { "Sales must have at least one payment" }
    }
}

data class CreateSalesDetailCommand(
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

data class CreateSalesPaymentCommand(
    val paymentMethodId: Long,
    val payAmt: BigDecimal,
    val saleType: Boolean,
    val paymentDate: LocalDateTime = LocalDateTime.now(),
    val changeAmt: BigDecimal = BigDecimal.ZERO
) {
    init {
        require(paymentMethodId > 0) { "Payment method ID must be positive" }
        require(payAmt > BigDecimal.ZERO) { "Payment amount must be positive" }
        require(changeAmt >= BigDecimal.ZERO) { "Change amount cannot be negative" }
    }
}