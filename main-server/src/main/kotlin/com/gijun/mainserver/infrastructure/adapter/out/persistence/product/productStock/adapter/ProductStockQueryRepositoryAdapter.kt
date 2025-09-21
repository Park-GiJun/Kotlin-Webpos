package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.adapter

import com.gijun.mainserver.application.port.out.product.productStock.ProductStockQueryRepository
import com.gijun.mainserver.domain.product.productStock.model.ProductStock
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.mapper.ProductStockPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.repository.ProductStockJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ProductStockQueryRepositoryAdapter(
    private val productStockJpaRepository: ProductStockJpaRepository
) : ProductStockQueryRepository {

    override fun findAll(): List<ProductStock> {
        return productStockJpaRepository.findAll()
            .map { ProductStockPersistenceMapper.toDomain(it) }
    }

    override fun findById(id: Long): ProductStock? {
        return productStockJpaRepository.findById(id)
            .map { ProductStockPersistenceMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByContainerId(containerId: Long): List<ProductStock> {
        return productStockJpaRepository.findByContainerId(containerId)
            .map { ProductStockPersistenceMapper.toDomain(it) }
    }

    override fun findByProductIdAndContainerId(productId: Long, containerId: Long): ProductStock? {
        return productStockJpaRepository.findByProductIdAndContainerId(productId, containerId)
            ?.let { ProductStockPersistenceMapper.toDomain(it) }
    }

    override fun existsById(id: Long): Boolean {
        return productStockJpaRepository.existsById(id)
    }
}