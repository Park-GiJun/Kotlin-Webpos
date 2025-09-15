package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa

import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity.SalesHeaderJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface SalesHeaderJpaRepository : JpaRepository<SalesHeaderJpaEntity, Long> {

    fun findByBillNo(billNo: String): SalesHeaderJpaEntity?

    fun findByStoreIdAndSaleDateBetween(
        storeId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<SalesHeaderJpaEntity>

    fun findByPosIdAndSaleDateBetween(
        posId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<SalesHeaderJpaEntity>

    fun findByHqIdAndSaleDateBetween(
        hqId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<SalesHeaderJpaEntity>

    @Query("""
        SELECT sh FROM SalesHeaderJpaEntity sh
        WHERE sh.storeId = :storeId
        AND sh.saleDate >= :startDate
        AND sh.saleDate <= :endDate
        ORDER BY sh.saleDate DESC
    """)
    fun findSalesByStoreAndDateRange(
        @Param("storeId") storeId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<SalesHeaderJpaEntity>

    @Query("""
        SELECT COUNT(sh) FROM SalesHeaderJpaEntity sh
        WHERE sh.storeId = :storeId
        AND sh.saleDate >= :startDate
        AND sh.saleDate <= :endDate
    """)
    fun countSalesByStoreAndDateRange(
        @Param("storeId") storeId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Long

    fun existsByBillNo(billNo: String): Boolean
}