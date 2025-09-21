package com.gijun.mainserver.application.dto.result.product.productStock

import java.math.BigDecimal

data class AdjustProductStockResult(
    val productStockId: Long,
    val productId: Long,
    val containerId: Long,
    val unitQtyBefore: BigDecimal,
    val usageQtyBefore: BigDecimal,
    val unitQtyAfter: BigDecimal,
    val usageQtyAfter: BigDecimal
)