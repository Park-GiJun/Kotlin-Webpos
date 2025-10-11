package com.gijun.posserver.application.port.out.stock

import java.math.BigDecimal

interface StockAdjustmentClient {
    fun adjustStock(request: StockAdjustmentRequest): StockAdjustmentResponse
}

data class StockAdjustmentRequest(
    val productId: Long,
    val storeId: Long,
    val adjustmentType: String,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal,
    val reason: String?
)

data class StockAdjustmentResponse(
    val productStockId: Long,
    val productId: Long,
    val containerId: Long,
    val unitQtyBefore: BigDecimal,
    val usageQtyBefore: BigDecimal,
    val unitQtyAfter: BigDecimal,
    val usageQtyAfter: BigDecimal
)
