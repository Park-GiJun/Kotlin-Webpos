package com.gijun.mainserver.domain.product.productCost.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductCost(
    val id: Long?,
    val hqId: Long?,
    val productId: Long?,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val price: BigDecimal
) {
    init {
        require(price >= BigDecimal.ZERO) { "Price cannot be negative" }
        require(startDate.isBefore(endDate)) { "Start date must be before end date" }
    }
}
