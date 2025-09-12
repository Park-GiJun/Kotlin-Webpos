package com.gijun.mainserver.application.dto.command.product.productStock

import com.gijun.mainserver.domain.common.vo.Quantity

data class CreateProductStockCommand(
    val productId: Long,
    val hqId: Long,
    val storeId: Long,
    val unitQty: Quantity,
    val usageQty: Quantity
)