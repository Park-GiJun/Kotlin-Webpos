package com.gijun.mainserver.application.port.out.product.productContainer

import com.gijun.mainserver.domain.product.productContainer.model.ProductContainer

interface ProductContainerCommandRepository {
    fun save(productContainer: ProductContainer): ProductContainer
    fun update(productContainer: ProductContainer): ProductContainer
    fun delete(id: Long)
}