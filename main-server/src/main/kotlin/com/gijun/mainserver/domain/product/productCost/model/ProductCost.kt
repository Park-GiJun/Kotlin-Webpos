package com.gijun.mainserver.domain.product.productCost.model

import com.gijun.mainserver.domain.common.vo.Money
import java.math.BigDecimal
import java.util.Date

data class ProductCost(
    val id: Long?,
    val hqId: Long?,
    val productId: Long?,
    val startDate: Date,
    val endDate: Date,
    val price: Money
)
