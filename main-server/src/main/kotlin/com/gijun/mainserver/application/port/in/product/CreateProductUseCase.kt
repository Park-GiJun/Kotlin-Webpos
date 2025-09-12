package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.CreateProductCommand
import com.gijun.mainserver.application.dto.result.product.CreateProductResult

interface CreateProductUseCase {
    fun createProductExecute(command: CreateProductCommand): CreateProductResult
}