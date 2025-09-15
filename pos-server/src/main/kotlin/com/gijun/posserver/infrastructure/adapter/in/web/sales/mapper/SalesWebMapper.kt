package com.gijun.posserver.infrastructure.adapter.`in`.web.sales.mapper

import com.gijun.posserver.application.dto.command.sales.CreateSalesCommand
import com.gijun.posserver.application.dto.command.sales.CreateSalesDetailCommand
import com.gijun.posserver.application.dto.command.sales.CreateSalesPaymentCommand
import com.gijun.posserver.application.dto.command.sales.UpdateSalesCommand
import com.gijun.posserver.application.dto.command.sales.UpdateSalesDetailCommand
import com.gijun.posserver.application.dto.command.sales.UpdateSalesPaymentCommand
import com.gijun.posserver.application.dto.result.sales.SalesResult
import com.gijun.posserver.application.dto.result.sales.SalesDetailResult
import com.gijun.posserver.application.dto.result.sales.SalesHeaderResult
import com.gijun.posserver.application.dto.result.sales.SalesPaymentResult
import com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto.*
import org.springframework.stereotype.Component

@Component
class SalesWebMapper {

    companion object {
        fun toCommand(request: CreateSalesRequest): CreateSalesCommand {
            return CreateSalesCommand(
                hqId = request.hqId,
                storeId = request.storeId,
                posId = request.posId,
                billNo = request.billNo,
                saleType = request.saleType,
                saleDate = request.saleDate,
                details = request.details.map { toCommand(it) },
                payments = request.payments.map { toCommand(it) }
            )
        }

        fun toCommand(request: CreateSalesDetailRequest): CreateSalesDetailCommand {
            return CreateSalesDetailCommand(
                lineNo = request.lineNo,
                productId = request.productId,
                productCode = request.productCode,
                qty = request.qty,
                unitAmt = request.unitAmt,
                saleQty = request.saleQty,
                saleType = request.saleType,
                saleDate = request.saleDate,
                saleAmt = request.saleAmt,
                payAmt = request.payAmt,
                dcAmt = request.dcAmt,
                couponAmt = request.couponAmt,
                cardAmt = request.cardAmt,
                cashAmt = request.cashAmt,
                voucherAmt = request.voucherAmt,
                promotionAmt = request.promotionAmt
            )
        }

        fun toCommand(request: CreateSalesPaymentRequest): CreateSalesPaymentCommand {
            return CreateSalesPaymentCommand(
                paymentMethodId = request.paymentMethodId,
                payAmt = request.payAmt,
                saleType = request.saleType,
                paymentDate = request.paymentDate,
                changeAmt = request.changeAmt
            )
        }

        fun toCommand(id: Long, request: UpdateSalesRequest): UpdateSalesCommand {
            return UpdateSalesCommand(
                id = id,
                hqId = request.hqId,
                storeId = request.storeId,
                posId = request.posId,
                billNo = request.billNo,
                saleType = request.saleType,
                saleDate = request.saleDate,
                details = request.details.map { toCommand(it) },
                payments = request.payments.map { toCommand(it) }
            )
        }

        fun toCommand(request: UpdateSalesDetailRequest): UpdateSalesDetailCommand {
            return UpdateSalesDetailCommand(
                id = request.id,
                lineNo = request.lineNo,
                productId = request.productId,
                productCode = request.productCode,
                qty = request.qty,
                unitAmt = request.unitAmt,
                saleQty = request.saleQty,
                saleType = request.saleType,
                saleDate = request.saleDate,
                saleAmt = request.saleAmt,
                payAmt = request.payAmt,
                dcAmt = request.dcAmt,
                couponAmt = request.couponAmt,
                cardAmt = request.cardAmt,
                cashAmt = request.cashAmt,
                voucherAmt = request.voucherAmt,
                promotionAmt = request.promotionAmt
            )
        }

        fun toCommand(request: UpdateSalesPaymentRequest): UpdateSalesPaymentCommand {
            return UpdateSalesPaymentCommand(
                id = request.id,
                paymentMethodId = request.paymentMethodId,
                payAmt = request.payAmt,
                saleType = request.saleType,
                paymentDate = request.paymentDate,
                changeAmt = request.changeAmt,
                isCompleted = request.isCompleted
            )
        }

        fun toResponse(result: SalesResult): SalesResponse {
            return SalesResponse(
                header = toResponse(result.header),
                details = result.details.map { toResponse(it) },
                payments = result.payments.map { toResponse(it) },
                totalSaleAmount = result.totalSaleAmount,
                totalPaymentAmount = result.totalPaymentAmount,
                totalDiscountAmount = result.totalDiscountAmount,
                isCompleteTransaction = result.isCompleteTransaction
            )
        }

        fun toResponse(result: SalesHeaderResult): SalesHeaderResponse {
            return SalesHeaderResponse(
                id = result.id,
                hqId = result.hqId,
                storeId = result.storeId,
                posId = result.posId,
                billNo = result.billNo,
                saleType = result.saleType,
                saleDate = result.saleDate,
                saleAmt = result.saleAmt,
                payAmt = result.payAmt,
                dcAmt = result.dcAmt,
                couponAmt = result.couponAmt,
                cardAmt = result.cardAmt,
                cashAmt = result.cashAmt,
                voucherAmt = result.voucherAmt,
                promotionAmt = result.promotionAmt,
                createdAt = result.createdAt,
                updatedAt = result.updatedAt
            )
        }

        fun toResponse(result: SalesDetailResult): SalesDetailResponse {
            return SalesDetailResponse(
                id = result.id,
                billId = result.billId,
                hqId = result.hqId,
                storeId = result.storeId,
                posId = result.posId,
                lineNo = result.lineNo,
                productId = result.productId,
                productCode = result.productCode,
                qty = result.qty,
                unitAmt = result.unitAmt,
                saleQty = result.saleQty,
                saleType = result.saleType,
                saleDate = result.saleDate,
                saleAmt = result.saleAmt,
                payAmt = result.payAmt,
                dcAmt = result.dcAmt,
                couponAmt = result.couponAmt,
                cardAmt = result.cardAmt,
                cashAmt = result.cashAmt,
                voucherAmt = result.voucherAmt,
                promotionAmt = result.promotionAmt
            )
        }

        fun toResponse(result: SalesPaymentResult): SalesPaymentResponse {
            return SalesPaymentResponse(
                id = result.id,
                billId = result.billId,
                hqId = result.hqId,
                storeId = result.storeId,
                posId = result.posId,
                paymentMethodId = result.paymentMethodId,
                payAmt = result.payAmt,
                saleType = result.saleType,
                paymentDate = result.paymentDate,
                changeAmt = result.changeAmt,
                isCompleted = result.isCompleted
            )
        }

        fun toCreateResponse(result: SalesResult): CreateSalesResponse {
            return CreateSalesResponse(
                id = result.header.id,
                billNo = result.header.billNo,
                totalSaleAmount = result.totalSaleAmount,
                totalPaymentAmount = result.totalPaymentAmount,
                isCompleteTransaction = result.isCompleteTransaction,
                createdAt = result.header.createdAt
            )
        }

        fun toUpdateResponse(result: SalesResult): UpdateSalesResponse {
            return UpdateSalesResponse(
                id = result.header.id,
                billNo = result.header.billNo,
                totalSaleAmount = result.totalSaleAmount,
                totalPaymentAmount = result.totalPaymentAmount,
                isCompleteTransaction = result.isCompleteTransaction,
                updatedAt = result.header.updatedAt
            )
        }
    }
}