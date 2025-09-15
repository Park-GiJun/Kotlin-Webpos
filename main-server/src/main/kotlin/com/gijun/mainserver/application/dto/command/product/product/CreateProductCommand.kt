package com.gijun.mainserver.application.dto.command.product.product

import com.gijun.mainserver.application.dto.command.product.productStock.CreateProductStockCommand
import com.gijun.mainserver.domain.product.product.model.ProductType
import java.math.BigDecimal

data class CreateProductCommand(
    val hqId: Long,
    val name: String,
    val price: BigDecimal,
    val productType: ProductType,
    val productCode: String,
    val supplyAmt: BigDecimal,
    val unit: String,
    val usageUnit: String,
    val initialStock: CreateProductStockCommand? = null
)