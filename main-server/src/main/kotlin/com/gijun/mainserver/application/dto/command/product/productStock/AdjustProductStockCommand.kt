package com.gijun.mainserver.application.dto.command.product.productStock

import com.gijun.mainserver.domain.common.vo.Quantity
import java.math.BigDecimal

data class AdjustProductStockCommand(
    val productId: Long,
    val storeId: Long,
    val adjustmentType: StockAdjustmentType,
    val unitQty: Quantity,
    val usageQty: Quantity,
    val reason: String? = null
)

enum class StockAdjustmentType {
    INCREASE,  // 재고 증가 (입고, 반품 등)
    DECREASE   // 재고 감소 (판매, 폐기 등)
}