package com.gijun.posserver.application.dto.command.sales

import java.math.BigDecimal
import java.time.LocalDateTime

data class UpdateSalesPaymentCommand(
    val id: Long?,
    val paymentMethodId: Long,
    val payAmt: BigDecimal,
    val saleType: Boolean,
    val paymentDate: LocalDateTime,
    val changeAmt: BigDecimal = BigDecimal.ZERO,
    val isCompleted: Boolean = false
) {
    init {
        require(paymentMethodId > 0) { "Payment method ID must be positive" }
        require(payAmt > BigDecimal.ZERO) { "Payment amount must be positive" }
        require(changeAmt >= BigDecimal.ZERO) { "Change amount cannot be negative" }
    }
}