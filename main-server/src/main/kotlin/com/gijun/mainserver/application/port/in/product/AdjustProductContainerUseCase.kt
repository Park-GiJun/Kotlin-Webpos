package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.productContainer.AdjustProductContainerCommand
import com.gijun.mainserver.application.dto.result.product.productContainer.AdjustProductContainerResult

interface AdjustProductContainerUseCase {
    fun adjustProductContainerExecute(command: AdjustProductContainerCommand): AdjustProductContainerResult
}