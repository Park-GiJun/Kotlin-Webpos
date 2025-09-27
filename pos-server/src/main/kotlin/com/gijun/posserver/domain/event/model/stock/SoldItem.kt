package com.gijun.posserver.domain.event.model.stock

import java.math.BigDecimal

data class SoldItem(
    val productId: Long,
    val productCode: String,
    val soldQuantity: BigDecimal,
    val unitPrice: BigDecimal
)