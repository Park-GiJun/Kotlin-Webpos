package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.product.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "product")
open class ProductJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "hq_id", nullable = false)
    val hqId: Long,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(name = "product_code", length = 50)
    val productCode: String?,

    @Column(name = "supply_amt", nullable = false, precision = 10, scale = 2)
    val supplyAmt: BigDecimal,

    @Column(name = "unit", nullable = false, length = 20)
    val unit: String,

    @Column(name = "usage_unit", nullable = false, length = 20)
    val usageUnit: String

) : BaseEntity() {


}