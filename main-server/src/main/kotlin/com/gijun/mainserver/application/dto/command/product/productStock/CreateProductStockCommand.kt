package com.gijun.mainserver.application.dto.command.product.productStock

import java.math.BigDecimal

data class CreateProductStockCommand(
    val productId: Long,
    val containerId: Long,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal
)