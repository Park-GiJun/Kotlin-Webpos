package com.gijun.mainserver.domain.product.productContainer.model

import java.math.BigDecimal

data class ProductContainer(
    val id: Long?,
    val hqId: Long,
    val containerId: Long,
    val containerName: String,
    val unitQty: BigDecimal,
    val usageQty: BigDecimal
) {
    fun increase(unitQtyToAdd: BigDecimal, usageQtyToAdd: BigDecimal): ProductContainer {
        return this.copy(
            unitQty = this.unitQty + unitQtyToAdd,
            usageQty = this.usageQty + usageQtyToAdd
        )
    }

    fun decrease(unitQtyToSubtract: BigDecimal, usageQtyToSubtract: BigDecimal): ProductContainer {
        val newUnitQty = this.unitQty - unitQtyToSubtract
        val newUsageQty = this.usageQty - usageQtyToSubtract

        require(newUnitQty >= BigDecimal.ZERO) {
            "컨테이너 단위 재고가 부족합니다. 현재: ${this.unitQty}, 요청: $unitQtyToSubtract"
        }
        require(newUsageQty >= BigDecimal.ZERO) {
            "컨테이너 사용 재고가 부족합니다. 현재: ${this.usageQty}, 요청: $usageQtyToSubtract"
        }

        return this.copy(
            unitQty = newUnitQty,
            usageQty = newUsageQty
        )
    }
}