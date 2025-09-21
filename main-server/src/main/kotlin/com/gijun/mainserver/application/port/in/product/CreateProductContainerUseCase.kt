package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.productContainer.CreateProductContainerCommand
import com.gijun.mainserver.application.dto.result.product.productContainer.CreateProductContainerResult

interface CreateProductContainerUseCase {
    fun createProductContainerExecute(command: CreateProductContainerCommand): CreateProductContainerResult
}