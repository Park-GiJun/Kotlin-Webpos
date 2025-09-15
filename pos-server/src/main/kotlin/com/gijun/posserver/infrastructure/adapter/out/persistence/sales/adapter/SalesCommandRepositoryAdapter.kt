package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.adapter

import com.gijun.posserver.application.port.out.sales.SalesCommandRepository
import com.gijun.posserver.domain.sales.model.Sales
import com.gijun.posserver.domain.sales.model.SalesHeader
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa.SalesDetailJpaRepository
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa.SalesHeaderJpaRepository
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.jpa.SalesPaymentJpaRepository
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.mapper.SalesPersistenceMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class SalesCommandRepositoryAdapter(
    private val salesHeaderJpaRepository: SalesHeaderJpaRepository,
    private val salesDetailJpaRepository: SalesDetailJpaRepository,
    private val salesPaymentJpaRepository: SalesPaymentJpaRepository
) : SalesCommandRepository {

    override fun save(sales: Sales): Sales {
        // Save header first to get ID
        val headerEntity = SalesPersistenceMapper.toEntity(sales.header)
        val savedHeaderEntity = salesHeaderJpaRepository.save(headerEntity)

        // Update billId in details and payments
        val detailEntities = sales.details.map { detail ->
            val updatedDetail = detail.copy(billId = savedHeaderEntity.id)
            SalesPersistenceMapper.toEntity(updatedDetail)
        }

        val paymentEntities = sales.payments.map { payment ->
            val updatedPayment = payment.copy(billId = savedHeaderEntity.id)
            SalesPersistenceMapper.toEntity(updatedPayment)
        }

        // Save details and payments
        val savedDetailEntities = salesDetailJpaRepository.saveAll(detailEntities)
        val savedPaymentEntities = salesPaymentJpaRepository.saveAll(paymentEntities)

        // Return complete sales domain object
        return SalesPersistenceMapper.toDomain(savedHeaderEntity, savedDetailEntities, savedPaymentEntities)
    }

    override fun update(sales: Sales): Sales {
        val headerId = sales.header.id
            ?: throw IllegalArgumentException("Sales header ID is required for update")

        // Update header
        val headerEntity = SalesPersistenceMapper.toEntity(sales.header)
        val savedHeaderEntity = salesHeaderJpaRepository.save(headerEntity)

        // Delete existing details and payments
        salesDetailJpaRepository.findByBillId(headerId).forEach { entity ->
            salesDetailJpaRepository.delete(entity)
        }
        salesPaymentJpaRepository.findByBillId(headerId).forEach { entity ->
            salesPaymentJpaRepository.delete(entity)
        }

        // Save new details and payments
        val detailEntities = sales.details.map { detail ->
            val updatedDetail = detail.copy(billId = headerId)
            SalesPersistenceMapper.toEntity(updatedDetail)
        }

        val paymentEntities = sales.payments.map { payment ->
            val updatedPayment = payment.copy(billId = headerId)
            SalesPersistenceMapper.toEntity(updatedPayment)
        }

        val savedDetailEntities = salesDetailJpaRepository.saveAll(detailEntities)
        val savedPaymentEntities = salesPaymentJpaRepository.saveAll(paymentEntities)

        // Return complete sales domain object
        return SalesPersistenceMapper.toDomain(savedHeaderEntity, savedDetailEntities, savedPaymentEntities)
    }

    override fun delete(id: Long) {
        // Delete details and payments first (foreign key constraint)
        salesDetailJpaRepository.findByBillId(id).forEach { entity ->
            salesDetailJpaRepository.delete(entity)
        }
        salesPaymentJpaRepository.findByBillId(id).forEach { entity ->
            salesPaymentJpaRepository.delete(entity)
        }

        // Delete header
        salesHeaderJpaRepository.deleteById(id)
    }

    override fun deleteByBillNo(billNo: String) {
        val headerEntity = salesHeaderJpaRepository.findByBillNo(billNo)
            ?: throw IllegalArgumentException("Sales with bill number $billNo not found")

        delete(headerEntity.id)
    }

    override fun saveHeader(header: SalesHeader): SalesHeader {
        val headerEntity = SalesPersistenceMapper.toEntity(header)
        val savedHeaderEntity = salesHeaderJpaRepository.save(headerEntity)
        return SalesPersistenceMapper.toDomain(savedHeaderEntity)
    }

    override fun updateHeader(header: SalesHeader): SalesHeader {
        val headerId = header.id
            ?: throw IllegalArgumentException("Sales header ID is required for update")

        if (!salesHeaderJpaRepository.existsById(headerId)) {
            throw IllegalArgumentException("Sales header with ID $headerId not found")
        }

        val headerEntity = SalesPersistenceMapper.toEntity(header)
        val savedHeaderEntity = salesHeaderJpaRepository.save(headerEntity)
        return SalesPersistenceMapper.toDomain(savedHeaderEntity)
    }
}