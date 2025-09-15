package com.gijun.mainserver.application.dto.command.product.productStock

import java.math.BigDecimal

data class CreateProductStockCommand(
    val productId: Long,
    val hqId: Long,
    val storeId: Long,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal
)