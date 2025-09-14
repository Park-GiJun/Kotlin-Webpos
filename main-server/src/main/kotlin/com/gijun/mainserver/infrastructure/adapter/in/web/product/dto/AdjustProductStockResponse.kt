package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import java.math.BigDecimal

data class AdjustProductStockResponse(
    val productStockId: Long,
    val productId: Long,
    val storeId: Long,
    val unitQtyBefore: BigDecimal,
    val usageQtyBefore: BigDecimal,
    val unitQtyAfter: BigDecimal,
    val usageQtyAfter: BigDecimal
)