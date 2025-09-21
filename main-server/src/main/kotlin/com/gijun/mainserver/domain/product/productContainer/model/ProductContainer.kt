package com.gijun.mainserver.domain.product.productContainer.model

data class ProductContainer(
    val id: Long?,
    val hqId: Long,
    val containerId: Long,
    val containerName: String
) {
    init {
        require(containerName.isNotBlank()) { "Container name cannot be blank" }
    }
}