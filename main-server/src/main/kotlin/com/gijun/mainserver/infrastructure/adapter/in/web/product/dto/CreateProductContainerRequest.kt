package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

data class CreateProductContainerRequest(
    val hqId: Long,
    val containerId: Long,
    val containerName: String
)