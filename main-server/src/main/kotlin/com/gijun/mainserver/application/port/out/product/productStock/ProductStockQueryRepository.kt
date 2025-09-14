package com.gijun.mainserver.application.port.out.product.productStock

import com.gijun.mainserver.domain.product.productStock.model.ProductStock

interface ProductStockQueryRepository {
    fun findAll(): List<ProductStock>
    fun findById(id: Long): ProductStock?
    fun findByHqId(hqId: Long): List<ProductStock>
    fun findByStoreId(storeId: Long): List<ProductStock>
    fun findByProductIdAndStoreId(productId: Long, storeId: Long): ProductStock?
    fun existsById(id: Long): Boolean
}