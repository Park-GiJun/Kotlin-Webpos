package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import java.math.BigDecimal

data class ProductResponse(
    val id: Long?,
    val hqId: Long?,
    val name: String,
    val price: BigDecimal,
    val productCode: String?,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String
)