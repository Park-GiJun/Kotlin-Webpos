package com.gijun.mainserver.application.port.out.product.productStock

import com.gijun.mainserver.domain.product.productStock.model.ProductStock

interface ProductStockQueryRepository {
    fun findAll(): List<ProductStock>
    fun findById(id: Long): ProductStock?
    fun findByContainerId(containerId: Long): List<ProductStock>
    fun findByProductIdAndContainerId(productId: Long, containerId: Long): ProductStock?
    fun existsById(id: Long): Boolean
}