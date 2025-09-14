package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import java.math.BigDecimal

data class CreateProductRequest(
    val hqId: Long,
    val name: String,
    val price: BigDecimal,
    val productCode: String?,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String,
    val initialStock: InitialStockRequest? = null
)

data class InitialStockRequest(
    val storeId: Long,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal
)