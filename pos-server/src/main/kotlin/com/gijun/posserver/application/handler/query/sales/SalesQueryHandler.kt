package com.gijun.posserver.application.handler.query.sales

import com.gijun.posserver.application.dto.query.sales.GetSalesByBillNoQuery
import com.gijun.posserver.application.dto.query.sales.GetSalesByIdQuery
import com.gijun.posserver.application.dto.query.sales.GetSalesByPosQuery
import com.gijun.posserver.application.dto.query.sales.GetSalesByStoreQuery
import com.gijun.posserver.application.dto.result.sales.SalesResult
import com.gijun.posserver.application.mapper.SalesApplicationMapper
import com.gijun.posserver.application.port.out.sales.SalesQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SalesQueryHandler(
    private val salesQueryRepository: SalesQueryRepository
) {

    fun getSalesById(query: GetSalesByIdQuery): SalesResult? {
        return salesQueryRepository.findById(query.id)
            ?.let { SalesApplicationMapper.toResult(it) }
    }

    fun getSalesByBillNo(query: GetSalesByBillNoQuery): SalesResult? {
        return salesQueryRepository.findByBillNo(query.billNo)
            ?.let { SalesApplicationMapper.toResult(it) }
    }

    fun getSalesByStore(query: GetSalesByStoreQuery): List<SalesResult> {
        val salesList = if (query.startDate != null && query.endDate != null) {
            salesQueryRepository.findByStoreIdAndDateRange(query.storeId, query.startDate, query.endDate)
        } else {
            salesQueryRepository.findByStoreId(query.storeId)
        }

        return salesList.map { SalesApplicationMapper.toResult(it) }
    }

    fun getSalesByPos(query: GetSalesByPosQuery): List<SalesResult> {
        val salesList = if (query.startDate != null && query.endDate != null) {
            salesQueryRepository.findByPosIdAndDateRange(query.posId, query.startDate, query.endDate)
        } else {
            salesQueryRepository.findByPosId(query.posId)
        }

        return salesList.map { SalesApplicationMapper.toResult(it) }
    }

    fun getAllSales(): List<SalesResult> {
        return salesQueryRepository.findAll()
            .map { SalesApplicationMapper.toResult(it) }
    }

    fun getSalesCount(storeId: Long, startDate: java.time.LocalDateTime, endDate: java.time.LocalDateTime): Long {
        return salesQueryRepository.countByStoreIdAndDateRange(storeId, startDate, endDate)
    }

    fun existsSalesById(id: Long): Boolean {
        return salesQueryRepository.existsById(id)
    }

    fun existsSalesByBillNo(billNo: String): Boolean {
        return salesQueryRepository.existsByBillNo(billNo)
    }
}