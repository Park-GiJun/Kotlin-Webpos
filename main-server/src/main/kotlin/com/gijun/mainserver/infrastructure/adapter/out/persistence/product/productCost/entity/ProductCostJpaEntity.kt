package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.productCost.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "product_cost")
open class ProductCostJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "hq_id", nullable = false)
    val hqId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDateTime,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDateTime,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal

) : BaseEntity() {


}