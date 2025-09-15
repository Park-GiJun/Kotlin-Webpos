package com.gijun.posserver.infrastructure.adapter.out.persistence.sales.mapper

import com.gijun.posserver.domain.sales.model.Sales
import com.gijun.posserver.domain.sales.model.SalesDetail
import com.gijun.posserver.domain.sales.model.SalesHeader
import com.gijun.posserver.domain.sales.model.SalesPayment
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity.SalesDetailJpaEntity
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity.SalesHeaderJpaEntity
import com.gijun.posserver.infrastructure.adapter.out.persistence.sales.entity.SalesPaymentJpaEntity
import org.springframework.stereotype.Component

class SalesPersistenceMapper {

    companion object {
        fun toDomain(entity: SalesHeaderJpaEntity): SalesHeader {
            return SalesHeader(
                id = entity.id,
                hqId = entity.hqId,
                storeId = entity.storeId,
                posId = entity.posId,
                billNo = entity.billNo,
                saleType = entity.saleType,
                saleDate = entity.saleDate,
                saleAmt = entity.saleAmt,
                payAmt = entity.payAmt,
                dcAmt = entity.dcAmt,
                couponAmt = entity.couponAmt,
                cardAmt = entity.cardAmt,
                cashAmt = entity.cashAmt,
                voucherAmt = entity.voucherAmt,
                promotionAmt = entity.promotionAmt,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }

        fun toDomain(entity: SalesDetailJpaEntity): SalesDetail {
            return SalesDetail(
                id = entity.id,
                billId = entity.billId,
                hqId = entity.hqId,
                storeId = entity.storeId,
                posId = entity.posId,
                lineNo = entity.lineNo,
                productId = entity.productId,
                productCode = entity.productCode,
                qty = entity.qty,
                unitAmt = entity.unitAmt,
                saleQty = entity.saleQty,
                saleType = entity.saleType,
                saleDate = entity.saleDate,
                saleAmt = entity.saleAmt,
                payAmt = entity.payAmt,
                dcAmt = entity.dcAmt,
                couponAmt = entity.couponAmt,
                cardAmt = entity.cardAmt,
                cashAmt = entity.cashAmt,
                voucherAmt = entity.voucherAmt,
                promotionAmt = entity.promotionAmt
            )
        }

        fun toDomain(entity: SalesPaymentJpaEntity): SalesPayment {
            return SalesPayment(
                id = entity.id,
                billId = entity.billId,
                hqId = entity.hqId,
                storeId = entity.storeId,
                posId = entity.posId,
                paymentMethodId = entity.paymentMethodId,
                payAmt = entity.payAmt,
                saleType = entity.saleType,
                paymentDate = entity.paymentDate,
                changeAmt = entity.changeAmt,
                isCompleted = entity.isCompleted
            )
        }

        fun toDomain(
            headerEntity: SalesHeaderJpaEntity,
            detailEntities: List<SalesDetailJpaEntity>,
            paymentEntities: List<SalesPaymentJpaEntity>
        ): Sales {
            val header = toDomain(headerEntity)
            val details = detailEntities.map { toDomain(it) }
            val payments = paymentEntities.map { toDomain(it) }

            return Sales(
                header = header,
                details = details,
                payments = payments
            )
        }

        fun toEntity(domain: SalesHeader): SalesHeaderJpaEntity {
            return SalesHeaderJpaEntity(
                id = domain.id ?: 0L,
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
                createdAt = domain.createdAt ?: java.time.LocalDateTime.now(),
                updatedAt = domain.updatedAt ?: java.time.LocalDateTime.now()
            )
        }

        fun toEntity(domain: SalesDetail): SalesDetailJpaEntity {
            return SalesDetailJpaEntity(
                id = domain.id ?: 0L,
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

        fun toEntity(domain: SalesPayment): SalesPaymentJpaEntity {
            return SalesPaymentJpaEntity(
                id = domain.id ?: 0L,
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