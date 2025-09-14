package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.adapter

import com.gijun.mainserver.application.port.out.product.productStock.ProductStockCommandRepository
import com.gijun.mainserver.domain.product.productStock.model.ProductStock
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.mapper.ProductStockPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.repository.ProductStockJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ProductStockCommandRepositoryAdapter(
    private val productStockJpaRepository: ProductStockJpaRepository
) : ProductStockCommandRepository {

    override fun save(productStock: ProductStock): ProductStock {
        val entity = ProductStockPersistenceMapper.toEntity(productStock)
        val savedEntity = productStockJpaRepository.save(entity)
        return ProductStockPersistenceMapper.toDomain(savedEntity)
    }

    override fun update(productStock: ProductStock): ProductStock {
        requireNotNull(productStock.id) { "ProductStock id must not be null for update" }
        val entity = ProductStockPersistenceMapper.toEntity(productStock)
        val updatedEntity = productStockJpaRepository.save(entity)
        return ProductStockPersistenceMapper.toDomain(updatedEntity)
    }

    override fun delete(id: Long) {
        productStockJpaRepository.deleteById(id)
    }
}