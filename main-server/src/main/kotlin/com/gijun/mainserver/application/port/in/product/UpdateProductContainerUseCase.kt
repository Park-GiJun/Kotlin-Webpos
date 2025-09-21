package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.productContainer.UpdateProductContainerCommand
import com.gijun.mainserver.application.dto.result.product.productContainer.UpdateProductContainerResult

interface UpdateProductContainerUseCase {
    fun updateProductContainerExecute(command: UpdateProductContainerCommand): UpdateProductContainerResult
}