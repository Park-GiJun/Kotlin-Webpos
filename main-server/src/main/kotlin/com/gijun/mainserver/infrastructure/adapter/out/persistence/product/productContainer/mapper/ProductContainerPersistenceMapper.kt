package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.mapper

import com.gijun.mainserver.domain.product.productContainer.model.ProductContainer
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.entity.ProductContainerJpaEntity

object ProductContainerPersistenceMapper {

    fun toDomain(entity: ProductContainerJpaEntity): ProductContainer {
        return ProductContainer(
            id = entity.id,
            hqId = entity.hqId,
            containerId = entity.containerId,
            containerName = entity.containerName,
            unitQty = entity.unitQty,
            usageQty = entity.usageQty
        )
    }

    fun toEntity(domain: ProductContainer): ProductContainerJpaEntity {
        return ProductContainerJpaEntity(
            id = domain.id ?: 0L,
            hqId = domain.hqId,
            containerId = domain.containerId,
            containerName = domain.containerName,
            unitQty = domain.unitQty,
            usageQty = domain.usageQty
        )
    }
}