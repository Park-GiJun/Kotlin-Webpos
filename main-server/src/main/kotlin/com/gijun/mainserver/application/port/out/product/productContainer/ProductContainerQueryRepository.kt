package com.gijun.mainserver.application.port.out.product.productContainer

import com.gijun.mainserver.domain.product.productContainer.model.ProductContainer

interface ProductContainerQueryRepository {
    fun findById(id: Long): ProductContainer?
    fun findByHqIdAndContainerId(hqId: Long, containerId: Long): ProductContainer?
    fun findByHqId(hqId: Long): List<ProductContainer>
    fun existsById(id: Long): Boolean
}