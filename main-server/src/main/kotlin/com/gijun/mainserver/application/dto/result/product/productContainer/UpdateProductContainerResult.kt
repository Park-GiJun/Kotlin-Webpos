package com.gijun.mainserver.application.dto.result.product.productContainer

data class UpdateProductContainerResult(
    val id: Long,
    val hqId: Long,
    val containerId: Long,
    val containerName: String
)