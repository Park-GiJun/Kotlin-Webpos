package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "sales_payment",
    indexes = [
        Index(name = "idx_sales_payment_bill", columnList = "billId"),
        Index(name = "idx_sales_payment_method", columnList = "paymentMethodId"),
        Index(name = "idx_sales_payment_store_date", columnList = "storeId,paymentDate")
    ]
)
class SalesPaymentJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val billId: Long,

    @Column(nullable = false)
    val hqId: Long,

    @Column(nullable = false)
    val storeId: Long,

    @Column(nullable = false)
    val posId: Long,

    @Column(nullable = false)
    val paymentMethodId: Long,

    @Column(nullable = false, precision = 19, scale = 2)
    val payAmt: BigDecimal,

    @Column(nullable = false)
    val saleType: Boolean,

    @Column(nullable = false)
    val paymentDate: LocalDateTime,

    @Column(nullable = false, precision = 19, scale = 2)
    val changeAmt: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val isCompleted: Boolean = false
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billId", insertable = false, updatable = false)
    lateinit var salesHeader: SalesHeaderJpaEntity
}