package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.UpdateProductCommand
import com.gijun.mainserver.application.dto.result.product.UpdateProductResult

interface UpdateProductUseCase {
    fun updateProductExecute(command: UpdateProductCommand): UpdateProductResult
}