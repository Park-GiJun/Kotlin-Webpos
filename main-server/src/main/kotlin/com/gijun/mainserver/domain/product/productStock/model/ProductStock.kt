package com.gijun.mainserver.domain.product.productStock.model

import java.math.BigDecimal

data class ProductStock(
    val id: Long?,
    val productId: Long,
    val hqId: Long,
    val storeId: Long,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal
) {
    fun increase(unitQtyToAdd: BigDecimal, usageQtyToAdd: BigDecimal): ProductStock {
        return this.copy(
            unitQty = this.unitQty + unitQtyToAdd,
            usageQty = this.usageQty + usageQtyToAdd
        )
    }

    fun decrease(unitQtyToSubtract: BigDecimal, usageQtyToSubtract: BigDecimal): ProductStock {
        val newUnitQty = this.unitQty - unitQtyToSubtract
        val newUsageQty = this.usageQty - usageQtyToSubtract

        require(newUnitQty >= BigDecimal.ZERO) {
            "단위 재고가 부족합니다. 현재: ${this.unitQty}, 요청: $unitQtyToSubtract"
        }
        require(newUsageQty >= BigDecimal.ZERO) {
            "사용 재고가 부족합니다. 현재: ${this.usageQty}, 요청: $usageQtyToSubtract"
        }

        return this.copy(
            unitQty = newUnitQty,
            usageQty = newUsageQty
        )
    }
}