package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa

import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity.SalesPaymentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface SalesPaymentJpaRepository : JpaRepository<SalesPaymentJpaEntity, Long> {

    fun findByBillId(billId: Long): List<SalesPaymentJpaEntity>

    fun findByPaymentMethodId(paymentMethodId: Long): List<SalesPaymentJpaEntity>

    fun findByStoreIdAndPaymentDateBetween(
        storeId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<SalesPaymentJpaEntity>

    @Query("""
        SELECT sp FROM SalesPaymentJpaEntity sp
        WHERE sp.paymentMethodId = :paymentMethodId
        AND sp.storeId = :storeId
        AND sp.paymentDate >= :startDate
        AND sp.paymentDate <= :endDate
        ORDER BY sp.paymentDate DESC
    """)
    fun findByPaymentMethodAndStoreAndDateRange(
        @Param("paymentMethodId") paymentMethodId: Long,
        @Param("storeId") storeId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<SalesPaymentJpaEntity>

    @Query("""
        SELECT SUM(sp.payAmt) FROM SalesPaymentJpaEntity sp
        WHERE sp.paymentMethodId = :paymentMethodId
        AND sp.storeId = :storeId
        AND sp.paymentDate >= :startDate
        AND sp.paymentDate <= :endDate
    """)
    fun sumPaymentAmountByMethodAndStore(
        @Param("paymentMethodId") paymentMethodId: Long,
        @Param("storeId") storeId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Long?

    fun findByIsCompletedFalse(): List<SalesPaymentJpaEntity>
}