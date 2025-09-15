package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "sales_detail",
    indexes = [
        Index(name = "idx_sales_detail_bill_line", columnList = "billId,lineNo"),
        Index(name = "idx_sales_detail_product", columnList = "productId"),
        Index(name = "idx_sales_detail_store_date", columnList = "storeId,saleDate")
    ]
)
class SalesDetailJpaEntity(
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
    val lineNo: Int,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false, length = 50)
    val productCode: String,

    @Column(nullable = false, precision = 19, scale = 3)
    val qty: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 2)
    val unitAmt: BigDecimal,

    @Column(nullable = false, precision = 19, scale = 3)
    val saleQty: BigDecimal,

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
    val promotionAmt: BigDecimal
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billId", insertable = false, updatable = false)
    lateinit var salesHeader: SalesHeaderJpaEntity
}