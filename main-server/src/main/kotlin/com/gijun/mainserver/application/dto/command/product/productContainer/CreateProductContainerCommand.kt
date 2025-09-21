package com.gijun.mainserver.application.dto.command.product.productContainer

data class CreateProductContainerCommand(
    val hqId: Long,
    val containerId: Long,
    val containerName: String
)