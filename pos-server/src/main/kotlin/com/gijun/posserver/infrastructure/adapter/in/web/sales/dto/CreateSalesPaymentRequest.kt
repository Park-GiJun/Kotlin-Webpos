package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateSalesPaymentRequest(
    @field:Positive(message = "Payment method ID must be positive")
    val paymentMethodId: Long,

    @field:Positive(message = "Payment amount must be positive")
    val payAmt: BigDecimal,

    val saleType: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val paymentDate: LocalDateTime = LocalDateTime.now(),

    val changeAmt: BigDecimal = BigDecimal.ZERO
)