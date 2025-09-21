package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.repository

import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.entity.ProductContainerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductContainerJpaRepository : JpaRepository<ProductContainerJpaEntity, Long> {
    fun findByHqIdAndContainerId(hqId: Long, containerId: Long): ProductContainerJpaEntity?
    fun findByHqId(hqId: Long): List<ProductContainerJpaEntity>
}