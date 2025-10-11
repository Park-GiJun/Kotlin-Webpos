package com.gijun.posserver.domain.sales.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesPayment(
    val id: Long?,
    val billId: Long,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val paymentMethodId: Long,
    val payAmt: BigDecimal,
    val saleType: Boolean,
    val paymentDate: LocalDateTime = LocalDateTime.now(),
    val changeAmt: BigDecimal = BigDecimal.ZERO,
    val isCompleted: Boolean = false
) {
    init {
        require(payAmt > BigDecimal.ZERO) { "Payment amount must be positive" }
        require(changeAmt >= BigDecimal.ZERO) { "Change amount cannot be negative" }
        require(hqId > 0) { "HQ ID must be positive" }
        require(storeId > 0) { "Store ID must be positive" }
        require(posId > 0) { "POS ID must be positive" }
        require(billId >= 0) { "Bill ID cannot be negative" }
        require(paymentMethodId > 0) { "Payment method ID must be positive" }
    }

    fun isValidPayment(): Boolean = payAmt > BigDecimal.ZERO && paymentMethodId > 0

    fun isCashPayment(): Boolean = paymentMethodId == CASH_PAYMENT_METHOD_ID

    fun isCardPayment(): Boolean = paymentMethodId == CARD_PAYMENT_METHOD_ID

    fun complete(): SalesPayment = this.copy(isCompleted = true)

    fun withChange(changeAmount: BigDecimal): SalesPayment {
        require(changeAmount >= BigDecimal.ZERO) { "Change amount cannot be negative" }
        require(isCashPayment()) { "Change can only be applied to cash payments" }

        return this.copy(changeAmt = changeAmount)
    }

    fun getNetPaymentAmount(): BigDecimal = payAmt - changeAmt

    companion object {
        const val CASH_PAYMENT_METHOD_ID = 1L
        const val CARD_PAYMENT_METHOD_ID = 2L
        const val VOUCHER_PAYMENT_METHOD_ID = 3L

        fun createCashPayment(
            billId: Long,
            hqId: Long,
            storeId: Long,
            posId: Long,
            payAmt: BigDecimal,
            saleType: Boolean,
            changeAmt: BigDecimal = BigDecimal.ZERO
        ): SalesPayment {
            return SalesPayment(
                id = null,
                billId = billId,
                hqId = hqId,
                storeId = storeId,
                posId = posId,
                paymentMethodId = CASH_PAYMENT_METHOD_ID,
                payAmt = payAmt,
                saleType = saleType,
                changeAmt = changeAmt
            )
        }

        fun createCardPayment(
            billId: Long,
            hqId: Long,
            storeId: Long,
            posId: Long,
            payAmt: BigDecimal,
            saleType: Boolean
        ): SalesPayment {
            return SalesPayment(
                id = null,
                billId = billId,
                hqId = hqId,
                storeId = storeId,
                posId = posId,
                paymentMethodId = CARD_PAYMENT_METHOD_ID,
                payAmt = payAmt,
                saleType = saleType
            )
        }
    }
}
