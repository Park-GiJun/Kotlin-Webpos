package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.adapter

import com.gijun.posserver.application.port.out.sales.SalesQueryRepository
import com.gijun.posserver.domain.sales.model.Sales
import com.gijun.posserver.domain.sales.model.SalesDetail
import com.gijun.posserver.domain.sales.model.SalesHeader
import com.gijun.posserver.domain.sales.model.SalesPayment
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa.SalesDetailJpaRepository
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa.SalesHeaderJpaRepository
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa.SalesPaymentJpaRepository
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.mapper.SalesPersistenceMapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SalesQueryRepositoryAdapter(
    private val salesHeaderJpaRepository: SalesHeaderJpaRepository,
    private val salesDetailJpaRepository: SalesDetailJpaRepository,
    private val salesPaymentJpaRepository: SalesPaymentJpaRepository,
) : SalesQueryRepository {

    override fun findById(id: Long): Sales? {
        val headerEntity = salesHeaderJpaRepository.findById(id).orElse(null) ?: return null
        val detailEntities = salesDetailJpaRepository.findByBillId(id)
        val paymentEntities = salesPaymentJpaRepository.findByBillId(id)

        return SalesPersistenceMapper.toDomain(headerEntity, detailEntities, paymentEntities)
    }

    override fun findByBillNo(billNo: String): Sales? {
        val headerEntity = salesHeaderJpaRepository.findByBillNo(billNo) ?: return null
        val detailEntities = salesDetailJpaRepository.findByBillId(headerEntity.id)
        val paymentEntities = salesPaymentJpaRepository.findByBillId(headerEntity.id)

        return SalesPersistenceMapper.toDomain(headerEntity, detailEntities, paymentEntities)
    }

    override fun findAll(): List<Sales> {
        return salesHeaderJpaRepository.findAll().map { headerEntity ->
            val detailEntities = salesDetailJpaRepository.findByBillId(headerEntity.id)
            val paymentEntities = salesPaymentJpaRepository.findByBillId(headerEntity.id)
            SalesPersistenceMapper.toDomain(headerEntity, detailEntities, paymentEntities)
        }
    }

    override fun findByStoreId(storeId: Long): List<Sales> {
        return salesHeaderJpaRepository.findAll()
            .filter { it.storeId == storeId }
            .map { headerEntity ->
                val detailEntities = salesDetailJpaRepository.findByBillId(headerEntity.id)
                val paymentEntities = salesPaymentJpaRepository.findByBillId(headerEntity.id)
                SalesPersistenceMapper.toDomain(headerEntity, detailEntities, paymentEntities)
            }
    }

    override fun findByPosId(posId: Long): List<Sales> {
        return salesHeaderJpaRepository.findAll()
            .filter { it.posId == posId }
            .map { headerEntity ->
                val detailEntities = salesDetailJpaRepository.findByBillId(headerEntity.id)
                val paymentEntities = salesPaymentJpaRepository.findByBillId(headerEntity.id)
                SalesPersistenceMapper.toDomain(headerEntity, detailEntities, paymentEntities)
            }
    }

    override fun findByStoreIdAndDateRange(
        storeId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Sales> {
        return salesHeaderJpaRepository.findByStoreIdAndSaleDateBetween(storeId, startDate, endDate)
            .map { headerEntity ->
                val detailEntities = salesDetailJpaRepository.findByBillId(headerEntity.id)
                val paymentEntities = salesPaymentJpaRepository.findByBillId(headerEntity.id)
                SalesPersistenceMapper.toDomain(headerEntity, detailEntities, paymentEntities)
            }
    }

    override fun findByPosIdAndDateRange(
        posId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Sales> {
        return salesHeaderJpaRepository.findByPosIdAndSaleDateBetween(posId, startDate, endDate)
            .map { headerEntity ->
                val detailEntities = salesDetailJpaRepository.findByBillId(headerEntity.id)
                val paymentEntities = salesPaymentJpaRepository.findByBillId(headerEntity.id)
                SalesPersistenceMapper.toDomain(headerEntity, detailEntities, paymentEntities)
            }
    }

    override fun findHeaderById(id: Long): SalesHeader? {
        return salesHeaderJpaRepository.findById(id).orElse(null)
            ?.let { SalesPersistenceMapper.toDomain(it) }
    }

    override fun findHeaderByBillNo(billNo: String): SalesHeader? {
        return salesHeaderJpaRepository.findByBillNo(billNo)
            ?.let { SalesPersistenceMapper.toDomain(it) }
    }

    override fun findDetailsByBillId(billId: Long): List<SalesDetail> {
        return salesDetailJpaRepository.findByBillId(billId)
            .map { SalesPersistenceMapper.toDomain(it) }
    }

    override fun findPaymentsByBillId(billId: Long): List<SalesPayment> {
        return salesPaymentJpaRepository.findByBillId(billId)
            .map { SalesPersistenceMapper.toDomain(it) }
    }

    override fun existsById(id: Long): Boolean {
        return salesHeaderJpaRepository.existsById(id)
    }

    override fun existsByBillNo(billNo: String): Boolean {
        return salesHeaderJpaRepository.existsByBillNo(billNo)
    }

    override fun countByStoreIdAndDateRange(
        storeId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Long {
        return salesHeaderJpaRepository.countSalesByStoreAndDateRange(storeId, startDate, endDate)
    }
}