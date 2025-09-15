package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

import com.gijun.mainserver.domain.product.product.model.ProductType
import java.math.BigDecimal

data class CreateProductRequest(
    val hqId: Long,
    val name: String,
    val price: BigDecimal,
    val productType: ProductType,
    val productCode: String?,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String,
    val initialStock: InitialStockRequest? = null
)