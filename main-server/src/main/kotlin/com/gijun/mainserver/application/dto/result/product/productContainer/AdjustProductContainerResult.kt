package com.gijun.mainserver.application.dto.result.product.productContainer

import java.math.BigDecimal

data class AdjustProductContainerResult(
    val productContainerId: Long,
    val hqId: Long,
    val containerId: Long,
    val containerName: String,
    val unitQtyBefore: BigDecimal,
    val usageQtyBefore: BigDecimal,
    val unitQtyAfter: BigDecimal,
    val usageQtyAfter: BigDecimal
)