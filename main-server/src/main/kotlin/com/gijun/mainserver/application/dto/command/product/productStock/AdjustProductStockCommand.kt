package com.gijun.mainserver.application.dto.command.product.productStock

import java.math.BigDecimal

data class AdjustProductStockCommand(
    val productId: Long,
    val storeId: Long,
    val adjustmentType: StockAdjustmentType,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal,
    val reason: String? = null
)