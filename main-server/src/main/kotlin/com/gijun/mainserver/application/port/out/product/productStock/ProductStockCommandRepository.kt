package com.gijun.mainserver.application.port.out.product.productStock

import com.gijun.mainserver.domain.product.productStock.model.ProductStock

interface ProductStockCommandRepository {
    fun save(productStock: ProductStock) : ProductStock
    fun update(productStock: ProductStock): ProductStock
    fun delete(id: Long)
}