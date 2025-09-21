package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.repository

import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.entity.ProductStockJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductStockJpaRepository : JpaRepository<ProductStockJpaEntity, Long> {
    fun findByProductId(productId: Long): List<ProductStockJpaEntity>
    fun findByContainerId(containerId: Long): List<ProductStockJpaEntity>
    fun findByProductIdAndContainerId(productId: Long, containerId: Long): ProductStockJpaEntity?
}