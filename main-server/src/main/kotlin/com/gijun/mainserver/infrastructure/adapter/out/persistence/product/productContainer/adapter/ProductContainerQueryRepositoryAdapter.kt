package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.adapter

import com.gijun.mainserver.application.port.out.product.productContainer.ProductContainerQueryRepository
import com.gijun.mainserver.domain.product.productContainer.model.ProductContainer
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.mapper.ProductContainerPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.repository.ProductContainerJpaRepository
import org.springframework.stereotype.Component

@Component
class ProductContainerQueryRepositoryAdapter(
    private val productContainerJpaRepository: ProductContainerJpaRepository
) : ProductContainerQueryRepository {

    override fun findById(id: Long): ProductContainer? {
        return productContainerJpaRepository.findById(id)
            .map { ProductContainerPersistenceMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByHqIdAndContainerId(hqId: Long, containerId: Long): ProductContainer? {
        return productContainerJpaRepository.findByHqIdAndContainerId(hqId, containerId)
            ?.let { ProductContainerPersistenceMapper.toDomain(it) }
    }

    override fun findByHqId(hqId: Long): List<ProductContainer> {
        return productContainerJpaRepository.findByHqId(hqId)
            .map { ProductContainerPersistenceMapper.toDomain(it) }
    }

    override fun existsById(id: Long): Boolean {
        return productContainerJpaRepository.existsById(id)
    }
}