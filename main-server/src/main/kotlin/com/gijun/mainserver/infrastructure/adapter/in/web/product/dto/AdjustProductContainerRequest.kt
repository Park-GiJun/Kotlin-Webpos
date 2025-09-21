package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import java.math.BigDecimal

data class AdjustProductContainerRequest(
    val hqId: Long,
    val containerId: Long,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal,
    val adjustmentType: String
)