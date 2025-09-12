package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.adapter

import com.gijun.mainserver.application.port.out.product.product.ProductQueryRepository
import com.gijun.mainserver.domain.product.product.model.Product
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.mapper.ProductPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.repository.ProductJpaRepository
import org.springframework.stereotype.Component

@Component
class ProductQueryRepositoryAdapter(
    private val productJpaRepository: ProductJpaRepository
) : ProductQueryRepository {

    override fun findAll(): List<Product> {
        return productJpaRepository.findAll()
            .map { ProductPersistenceMapper.toDomain(it) }
    }

    override fun findById(id: Long): Product? {
        return productJpaRepository.findById(id)
            .map { ProductPersistenceMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByHqId(hqId: Long): List<Product> {
        return productJpaRepository.findByHqId(hqId)
            .map { ProductPersistenceMapper.toDomain(it) }
    }

    override fun existsById(id: Long): Boolean {
        return productJpaRepository.existsById(id)
    }
}