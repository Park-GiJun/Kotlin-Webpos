package com.gijun.mainserver.application.dto.result.product.product

import com.gijun.mainserver.domain.common.vo.Money
import com.gijun.mainserver.domain.common.vo.ProductCode
import com.gijun.mainserver.domain.product.product.model.ProductType
import java.math.BigDecimal

data class ProductResult(
    val id: Long?,
    val hqId: Long?,
    val name: String,
    val price: Money,
    val productType: ProductType,
    val productCode: ProductCode?,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String
)