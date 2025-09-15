package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "sales_header",
    indexes = [
        Index(name = "idx_sales_header_bill_no", columnList = "billNo"),
        Index(name = "idx_sales_header_store_date", columnList = "storeId,saleDate"),
        Index(name = "idx_sales_header_pos_date", columnList = "posId,saleDate")
    ]
)
class SalesHeaderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val hqId: Long,

    @Column(nullable = false)
    val storeId: Long,

    @Column(nullable = false)
    val posId: Long,

    @Column(nullable = false, unique = true, length = 50)
    val billNo: String,

    @Column(nullable = false)
    val saleType: Boolean,

    @Column(nullable = false)
    val saleDate: LocalDateTime,

    @Column(nullable = false, precision = 19, scale = 2)
    val saleAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val payAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val dcAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val couponAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val cardAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val cashAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val voucherAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val promotionAmt: BigDecimal,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @OneToMany(mappedBy = "salesHeader", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val details: MutableList<SalesDetailJpaEntity> = mutableListOf()

    @OneToMany(mappedBy = "salesHeader", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val payments: MutableList<SalesPaymentJpaEntity> = mutableListOf()

    @PreUpdate
    fun onPreUpdate() {
        val now = LocalDateTime.now()
        // Use reflection to update the updatedAt field due to val constraint
        this::class.java.getDeclaredField("updatedAt").apply {
            isAccessible = true
            set(this@SalesHeaderJpaEntity, now)
        }
    }
}