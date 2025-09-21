package com.gijun.mainserver.domain.product.product.model

import java.math.BigDecimal

data class Product(
    val id: Long?,
    val hqId: Long,
    val name: String,
    val price: BigDecimal,
    val productType: ProductType,
    val productCode: String,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String
) {
    init {
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(price >= BigDecimal.ZERO) { "Price cannot be negative" }
        require(productCode.isNotBlank()) { "Product code cannot be blank" }
        require(supplyAmt >= BigDecimal.ZERO) { "Supply amount cannot be negative" }
        require(unit.isNotBlank()) { "Unit cannot be blank" }
        require(usageUnit.isNotBlank()) { "Usage unit cannot be blank" }
    }
}
