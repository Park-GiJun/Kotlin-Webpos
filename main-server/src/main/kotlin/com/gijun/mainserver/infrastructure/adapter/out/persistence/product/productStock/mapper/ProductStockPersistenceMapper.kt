package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.mapper

import com.gijun.mainserver.domain.product.productStock.model.ProductStock
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.entity.ProductStockJpaEntity

object ProductStockPersistenceMapper {

    fun toEntity(domain: ProductStock): ProductStockJpaEntity {
        return ProductStockJpaEntity(
            id = domain.id ?: 0L,
            productId = domain.productId,
            hqId = domain.hqId,
            storeId = domain.storeId,
            unitQty = domain.unitQty,
            usageQty = domain.usageQty
        )
    }

    fun toDomain(entity: ProductStockJpaEntity): ProductStock {
        return ProductStock(
            id = entity.id,
            productId = entity.productId,
            hqId = entity.hqId,
            storeId = entity.storeId,
            unitQty = entity.unitQty,
            usageQty = entity.usageQty
        )
    }
}