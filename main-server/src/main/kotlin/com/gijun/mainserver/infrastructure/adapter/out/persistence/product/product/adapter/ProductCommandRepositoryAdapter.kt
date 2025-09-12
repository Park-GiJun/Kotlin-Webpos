package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.adapter

import com.gijun.mainserver.application.port.out.product.product.ProductCommandRepository
import com.gijun.mainserver.domain.product.product.model.Product
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.mapper.ProductPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.repository.ProductJpaRepository
import org.springframework.stereotype.Component

@Component
class ProductCommandRepositoryAdapter(
    private val productJpaRepository: ProductJpaRepository
) : ProductCommandRepository {

    override fun save(product: Product): Product {
        val entity = ProductPersistenceMapper.toEntity(product)
        val savedEntity = productJpaRepository.save(entity)
        return ProductPersistenceMapper.toDomain(savedEntity)
    }

    override fun update(product: Product): Product {
        val existingEntity = productJpaRepository.findById(product.id!!)
            .orElseThrow { IllegalArgumentException("Product with id ${product.id} not found") }
        
        val updatedEntity = ProductPersistenceMapper.updateEntity(existingEntity, product)
        val savedEntity = productJpaRepository.save(updatedEntity)
        return ProductPersistenceMapper.toDomain(savedEntity)
    }

    override fun delete(id: Long) {
        productJpaRepository.deleteById(id)
    }
}