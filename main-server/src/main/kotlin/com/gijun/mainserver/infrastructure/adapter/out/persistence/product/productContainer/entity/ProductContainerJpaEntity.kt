package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productContainer.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "product_containers",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["hq_id", "container_id"])
    ]
)
class ProductContainerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "hq_id", nullable = false)
    val hqId: Long,

    @Column(name = "container_id", nullable = false)
    val containerId: Long,

    @Column(name = "container_name", nullable = false, length = 100)
    val containerName: String
) : BaseEntity()