package com.gijun.mainserver.application.port.out.product

import com.gijun.mainserver.domain.product.product.model.Product

interface ProductQueryRepository {
    fun findAll(): List<Product>
    fun findById(id: Long): Product?
    fun findByHqId(hqId: Long): List<Product>
    fun existsById(id: Long): Boolean
}