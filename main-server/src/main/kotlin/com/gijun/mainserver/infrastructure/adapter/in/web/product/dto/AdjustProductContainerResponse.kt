package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import java.math.BigDecimal

data class AdjustProductContainerResponse(
    val productContainerId: Long,
    val hqId: Long,
    val containerId: Long,
    val containerName: String,
    val unitQtyBefore: BigDecimal,
    val usageQtyBefore: BigDecimal,
    val unitQtyAfter: BigDecimal,
    val usageQtyAfter: BigDecimal
)