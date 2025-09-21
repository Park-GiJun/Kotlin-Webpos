package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.productContainer.DeleteProductContainerCommand
import com.gijun.mainserver.application.dto.result.product.productContainer.DeleteProductContainerResult

interface DeleteProductContainerUseCase {
    fun deleteProductContainerExecute(command: DeleteProductContainerCommand): DeleteProductContainerResult
}