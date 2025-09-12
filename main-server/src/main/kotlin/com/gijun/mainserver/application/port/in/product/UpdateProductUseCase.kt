package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.product.UpdateProductCommand
import com.gijun.mainserver.application.dto.result.product.product.UpdateProductResult

interface UpdateProductUseCase {
    fun updateProductExecute(command: UpdateProductCommand): UpdateProductResult
}