package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper

import com.gijun.mainserver.application.dto.command.product.CreateProductCommand
import com.gijun.mainserver.application.dto.command.product.UpdateProductCommand
import com.gijun.mainserver.application.dto.result.product.CreateProductResult
import com.gijun.mainserver.application.dto.result.product.ProductResult
import com.gijun.mainserver.application.dto.result.product.UpdateProductResult
import com.gijun.mainserver.domain.common.vo.Money
import com.gijun.mainserver.domain.common.vo.ProductCode
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.*

object ProductWebMapper {

    fun toCommand(request: CreateProductRequest): CreateProductCommand {
        return CreateProductCommand(
            hqId = request.hqId,
            name = request.name,
            price = Money(request.price),
            productCode = request.productCode?.let { ProductCode(it) },
            supplyAmt = request.supplyAmt,
            unit = request.unit,
            usageUnit = request.usageUnit
        )
    }

    fun toCommand(id: Long, request: UpdateProductRequest): UpdateProductCommand {
        return UpdateProductCommand(
            id = id,
            name = request.name,
            price = Money(request.price),
            productCode = request.productCode?.let { ProductCode(it) },
            supplyAmt = request.supplyAmt,
            unit = request.unit,
            usageUnit = request.usageUnit
        )
    }

    fun toResponse(result: CreateProductResult): CreateProductResponse {
        return CreateProductResponse(
            id = result.id
        )
    }

    fun toResponse(result: UpdateProductResult): UpdateProductResponse {
        return UpdateProductResponse(
            id = result.id
        )
    }

    fun toResponse(result: ProductResult): ProductResponse {
        return ProductResponse(
            id = result.id,
            hqId = result.hqId,
            name = result.name,
            price = result.price.value,
            productCode = result.productCode?.value,
            supplyAmt = result.supplyAmt,
            unit = result.unit,
            usageUnit = result.usageUnit
        )
    }
}