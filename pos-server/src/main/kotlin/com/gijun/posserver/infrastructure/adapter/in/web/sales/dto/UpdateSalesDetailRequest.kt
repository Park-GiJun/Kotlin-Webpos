package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class UpdateSalesDetailRequest(
    val id: Long?,

    @field:Positive(message = "Line number must be positive")
    val lineNo: Int,

    @field:Positive(message = "Product ID must be positive")
    val productId: Long,

    @field:NotBlank(message = "Product code cannot be blank")
    val productCode: String,

    @field:Positive(message = "Quantity must be positive")
    val qty: BigDecimal,

    @field:Positive(message = "Unit amount must be positive")
    val unitAmt: BigDecimal,

    @field:Positive(message = "Sale quantity must be positive")
    val saleQty: BigDecimal,

    val saleType: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val saleDate: LocalDateTime,

    val saleAmt: BigDecimal,
    val payAmt: BigDecimal,
    val dcAmt: BigDecimal = BigDecimal.ZERO,
    val couponAmt: BigDecimal = BigDecimal.ZERO,
    val cardAmt: BigDecimal = BigDecimal.ZERO,
    val cashAmt: BigDecimal = BigDecimal.ZERO,
    val voucherAmt: BigDecimal = BigDecimal.ZERO,
    val promotionAmt: BigDecimal = BigDecimal.ZERO
)