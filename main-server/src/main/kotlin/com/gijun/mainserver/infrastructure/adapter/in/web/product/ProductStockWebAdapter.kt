package com.gijun.mainserver.infrastructure.adapter.`in`.web.product

import com.gijun.mainserver.application.port.`in`.product.AdjustProductStockUseCase
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductStockRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductStockResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper.ProductStockWebMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/main/product-stock")
class ProductStockWebAdapter(
    private val adjustProductStockUseCase: AdjustProductStockUseCase
) {

    @PutMapping("/product/{productId}/store/{storeId}/adjust")
    fun adjustProductStock(
        @PathVariable productId: Long,
        @PathVariable storeId: Long,
        @RequestBody request: AdjustProductStockRequest
    ): ResponseEntity<AdjustProductStockResponse> {
        val command = ProductStockWebMapper.toCommand(productId, storeId, request)
        val result = adjustProductStockUseCase.adjustProductStockExecute(command)
        val response = ProductStockWebMapper.toResponse(result)

        return ResponseEntity.ok(response)
    }
}