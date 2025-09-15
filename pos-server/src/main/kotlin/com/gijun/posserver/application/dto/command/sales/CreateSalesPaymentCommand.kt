package com.gijun.posserver.application.dto.command.sales

import java.math.BigDecimal
import java.time.LocalDateTime

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