package com.gijun.mainserver.application.dto.result.product.product

import com.gijun.mainserver.domain.product.product.model.ProductType
import java.math.BigDecimal

data class ProductResult(
    val id: Long?,
    val hqId: Long?,
    val name: String,
    val price: BigDecimal,
    val productType: ProductType,
    val productCode: String,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String
)