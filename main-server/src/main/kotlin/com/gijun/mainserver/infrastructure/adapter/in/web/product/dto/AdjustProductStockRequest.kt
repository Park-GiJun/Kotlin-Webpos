package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import java.math.BigDecimal

data class AdjustProductStockRequest(
    val adjustmentType: String,  // "INCREASE" or "DECREASE"
    val unitQty: BigDecimal,
    val usageQty: BigDecimal,
    val reason: String? = null
)