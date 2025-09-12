package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.DeleteProductCommand
import com.gijun.mainserver.application.dto.result.product.DeleteProductResult

interface DeleteProductUseCase {
    fun deleteProductExecute(command: DeleteProductCommand): DeleteProductResult
}