package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto

data class UpdateProductContainerResponse(
    val id: Long,
    val hqId: Long,
    val containerId: Long,
    val containerName: String
)