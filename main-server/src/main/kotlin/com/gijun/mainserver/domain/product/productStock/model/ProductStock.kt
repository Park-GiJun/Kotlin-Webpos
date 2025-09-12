package com.gijun.mainserver.domain.product.productStock.model

import com.gijun.mainserver.domain.common.vo.Quantity
import java.math.BigDecimal

data class ProductStock(
    val id: Long?,
    val productId: Long,
    val hqId: Long,
    val storeId: Long,
    val unitQty: Quantity,
    val usageQty: Quantity
) {
}