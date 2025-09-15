package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import java.math.BigDecimal

data class InitialStockRequest(
    val storeId: Long,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal
)