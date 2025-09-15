package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesHeaderResponse(
    val id: Long?,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val billNo: String,
    val saleType: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val saleDate: LocalDateTime,

    val saleAmt: BigDecimal,
    val payAmt: BigDecimal,
    val dcAmt: BigDecimal,
    val couponAmt: BigDecimal,
    val cardAmt: BigDecimal,
    val cashAmt: BigDecimal,
    val voucherAmt: BigDecimal,
    val promotionAmt: BigDecimal,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime?,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime?
)