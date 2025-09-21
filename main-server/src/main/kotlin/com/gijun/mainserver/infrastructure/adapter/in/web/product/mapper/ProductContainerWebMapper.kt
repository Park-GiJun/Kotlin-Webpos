package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper

import com.gijun.mainserver.application.dto.command.product.productContainer.AdjustProductContainerCommand
import com.gijun.mainserver.application.dto.command.product.productContainer.ContainerAdjustmentType
import com.gijun.mainserver.application.dto.result.product.productContainer.AdjustProductContainerResult
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductContainerRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductContainerResponse

object ProductContainerWebMapper {

    fun toCommand(request: AdjustProductContainerRequest): AdjustProductContainerCommand {
        return AdjustProductContainerCommand(
            hqId = request.hqId,
            containerId = request.containerId,
            unitQty = request.unitQty,
            usageQty = request.usageQty,
            adjustmentType = ContainerAdjustmentType.valueOf(request.adjustmentType.uppercase())
        )
    }

    fun toResponse(result: AdjustProductContainerResult): AdjustProductContainerResponse {
        return AdjustProductContainerResponse(
            productContainerId = result.productContainerId,
            hqId = result.hqId,
            containerId = result.containerId,
            containerName = result.containerName,
            unitQtyBefore = result.unitQtyBefore,
            usageQtyBefore = result.usageQtyBefore,
            unitQtyAfter = result.unitQtyAfter,
            usageQtyAfter = result.usageQtyAfter
        )
    }
}