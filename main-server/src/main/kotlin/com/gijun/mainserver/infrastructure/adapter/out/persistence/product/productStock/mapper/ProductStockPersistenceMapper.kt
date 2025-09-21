package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.mapper

import com.gijun.mainserver.domain.product.productStock.model.ProductStock
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.entity.ProductStockJpaEntity

object ProductStockPersistenceMapper {

    fun toEntity(domain: ProductStock): ProductStockJpaEntity {
        return ProductStockJpaEntity(
            id = domain.id ?: 0L,
            productId = domain.productId,
            containerId = domain.containerId,
            unitQty = domain.unitQty,
            usageQty = domain.usageQty
        )
    }

    fun toDomain(entity: ProductStockJpaEntity): ProductStock {
        return ProductStock(
            id = entity.id,
            productId = entity.productId,
            containerId = entity.containerId,
            unitQty = entity.unitQty,
            usageQty = entity.usageQty
        )
    }
}