package com.gijun.mainserver.application.dto.command.product.productContainer

import java.math.BigDecimal

data class AdjustProductContainerCommand(
    val hqId: Long,
    val containerId: Long,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal,
    val adjustmentType: ContainerAdjustmentType
)