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

    override fun findByHqId(hqId: Long): List<ProductStock> {
        return productStockJpaRepository.findByHqId(hqId)
            .map { ProductStockPersistenceMapper.toDomain(it) }
    }

    override fun findByStoreId(storeId: Long): List<ProductStock> {
        return productStockJpaRepository.findByStoreId(storeId)
            .map { ProductStockPersistenceMapper.toDomain(it) }
    }

    override fun findByProductIdAndStoreId(productId: Long, storeId: Long): ProductStock? {
        return productStockJpaRepository.findByProductIdAndStoreId(productId, storeId)
            ?.let { ProductStockPersistenceMapper.toDomain(it) }
    }

    override fun existsById(id: Long): Boolean {
        return productStockJpaRepository.existsById(id)
    }
}