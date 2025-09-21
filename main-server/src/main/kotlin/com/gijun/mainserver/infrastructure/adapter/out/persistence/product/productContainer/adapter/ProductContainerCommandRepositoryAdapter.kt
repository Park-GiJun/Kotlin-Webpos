package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.adapter

import com.gijun.mainserver.application.port.out.product.productContainer.ProductContainerCommandRepository
import com.gijun.mainserver.domain.product.productContainer.model.ProductContainer
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.mapper.ProductContainerPersistenceMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.repository.ProductContainerJpaRepository
import org.springframework.stereotype.Component

@Component
class ProductContainerCommandRepositoryAdapter(
    private val productContainerJpaRepository: ProductContainerJpaRepository
) : ProductContainerCommandRepository {

    override fun save(productContainer: ProductContainer): ProductContainer {
        val entity = ProductContainerPersistenceMapper.toEntity(productContainer)
        val savedEntity = productContainerJpaRepository.save(entity)
        return ProductContainerPersistenceMapper.toDomain(savedEntity)
    }

    override fun update(productContainer: ProductContainer): ProductContainer {
        val entity = ProductContainerPersistenceMapper.toEntity(productContainer)
        val updatedEntity = productContainerJpaRepository.save(entity)
        return ProductContainerPersistenceMapper.toDomain(updatedEntity)
    }

    override fun delete(id: Long) {
        productContainerJpaRepository.deleteById(id)
    }
}