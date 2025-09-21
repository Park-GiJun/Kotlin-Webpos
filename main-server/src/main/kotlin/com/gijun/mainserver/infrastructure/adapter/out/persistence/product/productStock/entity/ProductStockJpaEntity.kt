package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productStock.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "product_stock")
open class ProductStockJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "container_id", nullable = false)
    val containerId: Long,

    @Column(name = "unit_qty", nullable = false, precision = 10, scale = 2)
    val unitQty: BigDecimal,

    @Column(name = "usage_qty", nullable = false, precision = 10, scale = 2)
    val usageQty: BigDecimal

) : BaseEntity() {


}