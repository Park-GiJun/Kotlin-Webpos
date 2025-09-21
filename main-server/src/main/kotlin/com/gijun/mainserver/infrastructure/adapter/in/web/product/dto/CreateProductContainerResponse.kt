package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

data class CreateProductContainerResponse(
    val id: Long,
    val hqId: Long,
    val containerId: Long,
    val containerName: String
)