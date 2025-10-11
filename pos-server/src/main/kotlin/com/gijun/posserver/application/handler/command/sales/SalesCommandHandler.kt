package com.gijun.posserver.application.handler.command.sales

import com.gijun.posserver.application.dto.command.sales.CreateSalesCommand
import com.gijun.posserver.application.dto.command.sales.UpdateSalesCommand
import com.gijun.posserver.application.dto.result.sales.SalesResult
import com.gijun.posserver.application.mapper.SalesApplicationMapper
import com.gijun.posserver.application.port.out.sales.SalesCommandRepository
import com.gijun.posserver.application.port.out.sales.SalesQueryRepository
import com.gijun.posserver.application.port.out.stock.StockAdjustmentClient
import com.gijun.posserver.application.port.out.stock.StockAdjustmentRequest
import com.gijun.posserver.domain.common.exception.DuplicateEntityException
import com.gijun.posserver.domain.common.exception.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class SalesCommandHandler(
    private val salesCommandRepository: SalesCommandRepository,
    private val salesQueryRepository: SalesQueryRepository,
    private val stockAdjustmentClient: StockAdjustmentClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createSales(command: CreateSalesCommand): SalesResult {
        if (salesQueryRepository.existsByBillNo(command.billNo)) {
            throw DuplicateEntityException("Sales", command.billNo)
        }

        val sales = SalesApplicationMapper.toDomain(command)

        val savedSales = salesCommandRepository.save(sales)

        // 판매 완료 후 재고 차감
        adjustStockForSales(savedSales)

        return SalesApplicationMapper.toResult(savedSales)
    }

    private fun adjustStockForSales(sales: com.gijun.posserver.domain.sales.model.Sales) {
        sales.details.forEach { detail ->
            try {
                val request = StockAdjustmentRequest(
                    productId = detail.productId,
                    storeId = detail.storeId,
                    adjustmentType = "DECREASE",
                    unitQty = BigDecimal.ZERO,
                    usageQty = detail.saleQty,
                    reason = "판매 (Bill No: ${sales.header.billNo})"
                )
                stockAdjustmentClient.adjustStock(request)
                logger.info("Stock adjusted for productId=${detail.productId}, qty=${detail.saleQty}")
            } catch (e: Exception) {
                logger.error("Failed to adjust stock for productId=${detail.productId}: ${e.message}", e)
                throw RuntimeException("재고 차감 실패: ${e.message}", e)
            }
        }
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