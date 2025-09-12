package com.gijun.mainserver.application.port.out.product

import com.gijun.mainserver.domain.product.product.model.Product

interface ProductCommandRepository {
    fun save(product: Product): Product
    fun update(product: Product): Product
    fun delete(id: Long)
}