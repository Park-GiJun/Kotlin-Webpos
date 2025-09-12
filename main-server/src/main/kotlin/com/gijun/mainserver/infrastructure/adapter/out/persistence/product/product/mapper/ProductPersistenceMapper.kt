package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.mapper

import com.gijun.mainserver.domain.common.vo.Money
import com.gijun.mainserver.domain.common.vo.ProductCode
import com.gijun.mainserver.domain.product.product.model.Product
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.entity.ProductJpaEntity

object ProductPersistenceMapper {

    fun toEntity(domain: Product): ProductJpaEntity {
        return ProductJpaEntity(
            id = domain.id ?: 0L,
            hqId = domain.hqId ?: 0L,
            name = domain.name,
            price = domain.price.value,
            productCode = domain.productCode?.value,
            supplyAmt = domain.supplyAmt,
            unit = domain.unit,
            usageUnit = domain.usageUnit
        )
    }

    fun toDomain(entity: ProductJpaEntity): Product {
        return Product(
            id = if (entity.id == 0L) null else entity.id,
            hqId = entity.hqId,
            name = entity.name,
            price = Money(entity.price),
            productCode = entity.productCode?.let { ProductCode(it) },
            supplyAmt = entity.supplyAmt,
            unit = entity.unit,
            usageUnit = entity.usageUnit
        )
    }

    fun updateEntity(existingEntity: ProductJpaEntity, domain: Product): ProductJpaEntity {
        return ProductJpaEntity(
            id = existingEntity.id,
            hqId = domain.hqId ?: existingEntity.hqId,
            name = domain.name,
            price = domain.price.value,
            productCode = domain.productCode?.value,
            supplyAmt = domain.supplyAmt,
            unit = domain.unit,
            usageUnit = domain.usageUnit
        )
    }
}