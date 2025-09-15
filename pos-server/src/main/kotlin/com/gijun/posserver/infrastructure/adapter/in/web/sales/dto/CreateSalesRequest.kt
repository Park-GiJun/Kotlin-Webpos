package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateSalesRequest(
    @field:Positive(message = "HQ ID must be positive")
    val hqId: Long,

    @field:Positive(message = "Store ID must be positive")
    val storeId: Long,

    @field:Positive(message = "POS ID must be positive")
    val posId: Long,

    @field:NotBlank(message = "Bill number cannot be blank")
    val billNo: String,

    val saleType: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val saleDate: LocalDateTime,

    @field:NotEmpty(message = "Sales must have at least one detail item")
    @field:Valid
    val details: List<CreateSalesDetailRequest>,

    @field:NotEmpty(message = "Sales must have at least one payment")
    @field:Valid
    val payments: List<CreateSalesPaymentRequest>
)