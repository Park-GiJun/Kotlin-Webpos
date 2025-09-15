package com.gijun.posserver.application.port.out.sales

import com.gijun.posserver.domain.sales.model.Sales
import com.gijun.posserver.domain.sales.model.SalesDetail
import com.gijun.posserver.domain.sales.model.SalesHeader
import com.gijun.posserver.domain.sales.model.SalesPayment
import java.time.LocalDateTime

interface SalesQueryRepository {
    fun findById(id: Long): Sales?
    fun findByBillNo(billNo: String): Sales?
    fun findAll(): List<Sales>
    fun findByStoreId(storeId: Long): List<Sales>
    fun findByPosId(posId: Long): List<Sales>
    fun findByStoreIdAndDateRange(storeId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<Sales>
    fun findByPosIdAndDateRange(posId: Long, startDate: LocalDateTime, endDate: LocalDateTime): List<Sales>

    fun findHeaderById(id: Long): SalesHeader?
    fun findHeaderByBillNo(billNo: String): SalesHeader?
    fun findDetailsByBillId(billId: Long): List<SalesDetail>
    fun findPaymentsByBillId(billId: Long): List<SalesPayment>

    fun existsById(id: Long): Boolean
    fun existsByBillNo(billNo: String): Boolean
    fun countByStoreIdAndDateRange(storeId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Long
}