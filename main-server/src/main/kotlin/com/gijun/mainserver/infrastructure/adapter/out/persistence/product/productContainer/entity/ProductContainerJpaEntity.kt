package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(
    name = "product_containers",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["hq_id", "container_id"])
    ]
)
class ProductContainerJpaEntity(
    @Id
    val id : Long,

    @Column(name = "hq_id", nullable = false)
    val hqId: Long,

    @Column(name = "container_id", nullable = false)
    val containerId: Long,

    @Column(name = "container_name", nullable = false, length = 100)
    val containerName: String,

    @Column(name = "unit_qty", nullable = false, precision = 19, scale = 2)
    val unitQty: BigDecimal,

    @Column(name = "usage_qty", nullable = false, precision = 19, scale = 2)
    val usageQty: BigDecimal
) : BaseEntity()