package com.gijun.mainserver.application.dto.command.product.product

import com.gijun.mainserver.application.dto.command.product.productStock.CreateProductStockCommand
import com.gijun.mainserver.domain.common.vo.Money
import com.gijun.mainserver.domain.common.vo.ProductCode
import java.math.BigDecimal

data class CreateProductCommand(
    val hqId: Long,
    val name: String,
    val price: Money,
    val productCode: ProductCode?,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String,
    val initialStock: CreateProductStockCommand? = null
)