package com.gijun.mainserver.domain.product.productStock.model

import com.gijun.mainserver.domain.common.vo.Quantity
import java.math.BigDecimal

data class ProductStock(
    val id: Long?,
    val productId: Long,
    val hqId: Long,
    val storeId: Long,
    val unitQty: Quantity,
    val usageQty: Quantity
) {
    fun increase(unitQtyToAdd: Quantity, usageQtyToAdd: Quantity): ProductStock {
        return this.copy(
            unitQty = Quantity(this.unitQty.value + unitQtyToAdd.value),
            usageQty = Quantity(this.usageQty.value + usageQtyToAdd.value)
        )
    }

    fun decrease(unitQtyToSubtract: Quantity, usageQtyToSubtract: Quantity): ProductStock {
        val newUnitQty = this.unitQty.value - unitQtyToSubtract.value
        val newUsageQty = this.usageQty.value - usageQtyToSubtract.value

        require(newUnitQty >= BigDecimal.ZERO) {
            "단위 재고가 부족합니다. 현재: ${this.unitQty.value}, 요청: ${unitQtyToSubtract.value}"
        }
        require(newUsageQty >= BigDecimal.ZERO) {
            "사용 재고가 부족합니다. 현재: ${this.usageQty.value}, 요청: ${usageQtyToSubtract.value}"
        }

        return this.copy(
            unitQty = Quantity(newUnitQty),
            usageQty = Quantity(newUsageQty)
        )
    }
}