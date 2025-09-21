package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper

import com.gijun.mainserver.application.dto.command.product.productStock.AdjustProductStockCommand
import com.gijun.mainserver.application.dto.command.product.productStock.StockAdjustmentType
import com.gijun.mainserver.application.dto.result.product.productStock.AdjustProductStockResult
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductStockRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductStockResponse

object ProductStockWebMapper {

    fun toCommand(
        productId: Long,
        containerId: Long,
        request: AdjustProductStockRequest
    ): AdjustProductStockCommand {
        return AdjustProductStockCommand(
            productId = productId,
            containerId = containerId,
            adjustmentType = StockAdjustmentType.valueOf(request.adjustmentType),
            unitQty = request.unitQty,
            usageQty = request.usageQty,
            reason = request.reason
        )
    }

    fun toResponse(result: AdjustProductStockResult): AdjustProductStockResponse {
        return AdjustProductStockResponse(
            productStockId = result.productStockId,
            productId = result.productId,
            containerId = result.containerId,
            unitQtyBefore = result.unitQtyBefore,
            usageQtyBefore = result.usageQtyBefore,
            unitQtyAfter = result.unitQtyAfter,
            usageQtyAfter = result.usageQtyAfter
        )
    }
}