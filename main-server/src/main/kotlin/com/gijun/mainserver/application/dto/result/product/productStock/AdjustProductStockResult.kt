package com.gijun.mainserver.application.dto.result.product.productStock

import com.gijun.mainserver.domain.common.vo.Quantity

data class AdjustProductStockResult(
    val productStockId: Long,
    val productId: Long,
    val storeId: Long,
    val unitQtyBefore: Quantity,
    val usageQtyBefore: Quantity,
    val unitQtyAfter: Quantity,
    val usageQtyAfter: Quantity
)