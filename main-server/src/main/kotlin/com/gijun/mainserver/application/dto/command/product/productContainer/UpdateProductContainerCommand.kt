package com.gijun.mainserver.application.dto.command.product.productContainer

data class UpdateProductContainerCommand(
    val id: Long,
    val hqId: Long,
    val containerId: Long,
    val containerName: String
)