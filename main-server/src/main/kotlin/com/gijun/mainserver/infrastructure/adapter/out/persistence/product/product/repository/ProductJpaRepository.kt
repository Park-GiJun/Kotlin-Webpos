package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.repository

import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.entity.ProductJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductJpaRepository : JpaRepository<ProductJpaEntity, Long> {
    fun findByHqId(hqId: Long): List<ProductJpaEntity>
}