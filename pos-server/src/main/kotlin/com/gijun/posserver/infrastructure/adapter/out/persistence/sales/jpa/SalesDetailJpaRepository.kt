package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa

import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity.SalesDetailJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface SalesDetailJpaRepository : JpaRepository<SalesDetailJpaEntity, Long> {

    fun findByBillId(billId: Long): List<SalesDetailJpaEntity>

    fun findByBillIdAndLineNo(billId: Long, lineNo: Int): SalesDetailJpaEntity?

    fun findByProductId(productId: Long): List<SalesDetailJpaEntity>

    fun findByStoreIdAndSaleDateBetween(
        storeId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<SalesDetailJpaEntity>

    @Query("""
        SELECT sd FROM SalesDetailJpaEntity sd
        WHERE sd.productId = :productId
        AND sd.saleDate >= :startDate
        AND sd.saleDate <= :endDate
        ORDER BY sd.saleDate DESC
    """)
    fun findByProductIdAndDateRange(
        @Param("productId") productId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<SalesDetailJpaEntity>

    @Query("""
        SELECT SUM(sd.saleQty) FROM SalesDetailJpaEntity sd
        WHERE sd.productId = :productId
        AND sd.storeId = :storeId
        AND sd.saleDate >= :startDate
        AND sd.saleDate <= :endDate
    """)
    fun sumSalesQuantityByProductAndStore(
        @Param("productId") productId: Long,
        @Param("storeId") storeId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Long?
}