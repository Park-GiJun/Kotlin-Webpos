package com.gijun.posserver.application.mapper

import com.gijun.posserver.application.dto.command.sales.CreateSalesCommand
import com.gijun.posserver.application.dto.command.sales.CreateSalesDetailCommand
import com.gijun.posserver.application.dto.command.sales.CreateSalesPaymentCommand
import com.gijun.posserver.application.dto.command.sales.UpdateSalesCommand
import com.gijun.posserver.application.dto.result.sales.SalesDetailResult
import com.gijun.posserver.application.dto.result.sales.SalesHeaderResult
import com.gijun.posserver.application.dto.result.sales.SalesPaymentResult
import com.gijun.posserver.application.dto.result.sales.SalesResult
import com.gijun.posserver.domain.sales.model.Sales
import com.gijun.posserver.domain.sales.model.SalesDetail
import com.gijun.posserver.domain.sales.model.SalesHeader
import com.gijun.posserver.domain.sales.model.SalesPayment
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class SalesApplicationMapper {

    companion object {
        fun toDomain(command: CreateSalesCommand): Sales {
            val header = SalesHeader(
                id = null,
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

            val details = command.details.map { detailCommand ->
                toDomain(detailCommand, command.hqId, command.storeId, command.posId, 0L) // billId will be set after header save
            }

            val payments = command.payments.map { paymentCommand ->
                toDomain(paymentCommand, command.hqId, command.storeId, command.posId, 0L) // billId will be set after header save
            }

            return Sales(
                header = header,
                details = details,
                payments = payments
            )
        }

        fun toDomain(command: CreateSalesDetailCommand, hqId: Long, storeId: Long, posId: Long, billId: Long): SalesDetail {
            return SalesDetail(
                id = null,
                billId = billId,
                hqId = hqId,
                storeId = storeId,
                posId = posId,
                lineNo = command.lineNo,
                productId = command.productId,
                productCode = command.productCode,
                qty = command.qty,
                unitAmt = command.unitAmt,
                saleQty = command.saleQty,
                saleType = command.saleType,
                saleDate = command.saleDate,
                saleAmt = command.saleAmt,
                payAmt = command.payAmt,
                dcAmt = command.dcAmt,
                couponAmt = command.couponAmt,
                cardAmt = command.cardAmt,
                cashAmt = command.cashAmt,
                voucherAmt = command.voucherAmt,
                promotionAmt = command.promotionAmt
            )
        }

        fun toDomain(command: CreateSalesPaymentCommand, hqId: Long, storeId: Long, posId: Long, billId: Long): SalesPayment {
            return SalesPayment(
                id = null,
                billId = billId,
                hqId = hqId,
                storeId = storeId,
                posId = posId,
                paymentMethodId = command.paymentMethodId,
                payAmt = command.payAmt,
                saleType = command.saleType,
                paymentDate = command.paymentDate,
                changeAmt = command.changeAmt,
                isCompleted = false
            )
        }

        fun toResult(domain: Sales): SalesResult {
            return SalesResult(
                header = toHeaderResult(domain.header),
                details = domain.details.map { toDetailResult(it) },
                payments = domain.payments.map { toPaymentResult(it) },
                totalSaleAmount = domain.getTotalSaleAmount(),
                totalPaymentAmount = domain.getTotalPaymentAmount(),
                totalDiscountAmount = domain.getTotalDiscountAmount(),
                isCompleteTransaction = domain.isCompleteTransaction()
            )
        }

        fun toHeaderResult(domain: SalesHeader): SalesHeaderResult {
            return SalesHeaderResult(
                id = domain.id,
                hqId = domain.hqId,
                storeId = domain.storeId,
                posId = domain.posId,
                billNo = domain.billNo,
                saleType = domain.saleType,
                saleDate = domain.saleDate,
                saleAmt = domain.saleAmt,
                payAmt = domain.payAmt,
                dcAmt = domain.dcAmt,
                couponAmt = domain.couponAmt,
                cardAmt = domain.cardAmt,
                cashAmt = domain.cashAmt,
                voucherAmt = domain.voucherAmt,
                promotionAmt = domain.promotionAmt,
                createdAt = domain.createdAt,
                updatedAt = domain.updatedAt
            )
        }

        fun toDetailResult(domain: SalesDetail): SalesDetailResult {
            return SalesDetailResult(
                id = domain.id,
                billId = domain.billId,
                hqId = domain.hqId,
                storeId = domain.storeId,
                posId = domain.posId,
                lineNo = domain.lineNo,
                productId = domain.productId,
                productCode = domain.productCode,
                qty = domain.qty,
                unitAmt = domain.unitAmt,
                saleQty = domain.saleQty,
                saleType = domain.saleType,
                saleDate = domain.saleDate,
                saleAmt = domain.saleAmt,
                payAmt = domain.payAmt,
                dcAmt = domain.dcAmt,
                couponAmt = domain.couponAmt,
                cardAmt = domain.cardAmt,
                cashAmt = domain.cashAmt,
                voucherAmt = domain.voucherAmt,
                promotionAmt = domain.promotionAmt
            )
        }

        fun toPaymentResult(domain: SalesPayment): SalesPaymentResult {
            return SalesPaymentResult(
                id = domain.id,
                billId = domain.billId,
                hqId = domain.hqId,
                storeId = domain.storeId,
                posId = domain.posId,
                paymentMethodId = domain.paymentMethodId,
                payAmt = domain.payAmt,
                saleType = domain.saleType,
                paymentDate = domain.paymentDate,
                changeAmt = domain.changeAmt,
                isCompleted = domain.isCompleted
            )
        }
    }
}