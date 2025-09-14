package com.gijun.mainserver.domain.product.product.model

import com.gijun.mainserver.domain.common.vo.Money
import com.gijun.mainserver.domain.common.vo.ProductCode
import java.math.BigDecimal

data class Product(
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
