package com.gijun.posserver.application.handler.command.sales

import com.gijun.posserver.application.dto.command.sales.CreateSalesCommand
import com.gijun.posserver.application.dto.command.sales.UpdateSalesCommand
import com.gijun.posserver.application.dto.result.sales.SalesResult
import com.gijun.posserver.application.mapper.SalesApplicationMapper
import com.gijun.posserver.application.port.out.event.OutboxEventRepository
import com.gijun.posserver.application.port.out.sales.SalesCommandRepository
import com.gijun.posserver.application.port.out.sales.SalesQueryRepository
import com.gijun.posserver.domain.common.exception.DuplicateEntityException
import com.gijun.posserver.domain.common.exception.EntityNotFoundException
import com.gijun.posserver.domain.event.model.OutboxEvent
import com.gijun.posserver.domain.event.model.OutboxStatus
import com.gijun.posserver.domain.event.model.stock.SoldItem
import com.gijun.posserver.domain.event.model.stock.StockReductionRequestedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class SalesCommandHandler(
    private val salesCommandRepository: SalesCommandRepository,
    private val salesQueryRepository: SalesQueryRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {

    fun createSales(command: CreateSalesCommand): SalesResult {
        if (salesQueryRepository.existsByBillNo(command.billNo)) {
            throw DuplicateEntityException("Sales", command.billNo)
        }

        val sales = SalesApplicationMapper.toDomain(command)

        val savedSales = salesCommandRepository.save(sales)

        val stockReductionEvent = StockReductionRequestedEvent(
            eventId = UUID.randomUUID().toString(),
            salesId = savedSales.header.billNo,
            storeId = savedSales.header.storeId,
            posId = savedSales.header.posId,
            soldItems = sales.details.map {
                detail -> SoldItem(
                    productId = detail.productId,
                    productCode = detail.productCode,
                    soldQuantity = detail.saleQty,
                    unitPrice = detail.unitAmt
                )
            },
            salesDate = savedSales.header.saleDate
        )

        val outboxEvent = OutboxEvent(
            id = null,
            eventId = stockReductionEvent.eventId,
            eventType = "STOCK_REDUCTION_REQUESTED",
            aggregateType = "SALES",
            aggregateId = savedSales.header.billNo,
            eventData = objectMapper.writeValueAsString(stockReductionEvent),
            status = OutboxStatus.PENDING,
            retryCount = 0
        )

        outboxEventRepository.save(outboxEvent)

        return SalesApplicationMapper.toResult(savedSales)
    }

    fun updateSales(command: UpdateSalesCommand): SalesResult {
        // Check if sales exists
        val existingSales = salesQueryRepository.findById(command.id)
            ?: throw EntityNotFoundException("Sales", command.id)

        // Convert command to domain object
        val updatedSales = existingSales.copy(
            header = existingSales.header.copy(
                hqId = command.hqId,
                storeId = command.storeId,
                posId = command.posId,
                billNo = command.billNo,
                saleType = command.saleType,
                saleDate = command.saleDate,
                saleAmt = command.details.sumOf { it.saleAmt },
                payAmt = command.payments.sumOf { it.payAmt },
                dcAmt = command.details.sumOf { it.dcAmt },
                couponAmt = command.details.sumOf { it.couponAmt },
                cardAmt = command.details.sumOf { it.cardAmt },
                cashAmt = command.details.sumOf { it.cashAmt },
                voucherAmt = command.details.sumOf { it.voucherAmt },
                promotionAmt = command.details.sumOf { it.promotionAmt }
            )
        )

        // Save updated sales
        val savedSales = salesCommandRepository.update(updatedSales)

        // Return result
        return SalesApplicationMapper.toResult(savedSales)
    }

    fun deleteSales(id: Long) {
        // Check if sales exists
        if (!salesQueryRepository.existsById(id)) {
            throw EntityNotFoundException("Sales", id)
        }

        // Delete sales
        salesCommandRepository.delete(id)
    }

    fun deleteSalesByBillNo(billNo: String) {
        // Check if sales exists
        if (!salesQueryRepository.existsByBillNo(billNo)) {
            throw EntityNotFoundException("Sales", billNo)
        }

        // Delete sales
        salesCommandRepository.deleteByBillNo(billNo)
    }
}