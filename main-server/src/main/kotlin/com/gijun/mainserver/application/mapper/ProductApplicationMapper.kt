package com.gijun.mainserver.application.mapper

import com.gijun.mainserver.application.dto.command.product.CreateProductCommand
import com.gijun.mainserver.application.dto.command.product.UpdateProductCommand
import com.gijun.mainserver.application.dto.result.product.CreateProductResult
import com.gijun.mainserver.application.dto.result.product.DeleteProductResult
import com.gijun.mainserver.application.dto.result.product.ProductResult
import com.gijun.mainserver.application.dto.result.product.UpdateProductResult
import com.gijun.mainserver.domain.product.product.model.Product

object ProductApplicationMapper {

    fun toCreateResult(product: Product): CreateProductResult {
        return CreateProductResult(
            id = product.id!!
        )
    }

    fun toUpdateResult(product: Product): UpdateProductResult {
        return UpdateProductResult(
            id = product.id!!
        )
    }

    fun toDeleteResult(id: Long): DeleteProductResult {
        return DeleteProductResult(id = id)
    }

    fun toProductResult(product: Product): ProductResult {
        return ProductResult(
            id = product.id,
            hqId = product.hqId,
            name = product.name,
            price = product.price,
            productCode = product.productCode,
            supplyAmt = product.supplyAmt,
            unit = product.unit,
            usageUnit = product.usageUnit
        )
    }

    fun toDomain(command: CreateProductCommand): Product {
        return Product(
            id = null,
            hqId = command.hqId,
            name = command.name,
            price = command.price,
            productCode = command.productCode,
            supplyAmt = command.supplyAmt,
            unit = command.unit,
            usageUnit = command.usageUnit
        )
    }

    fun toDomain(command: UpdateProductCommand): Product {
        return Product(
            id = command.id,
            hqId = null,
            name = command.name,
            price = command.price,
            productCode = command.productCode,
            supplyAmt = command.supplyAmt,
            unit = command.unit,
            usageUnit = command.usageUnit
        )
    }
}