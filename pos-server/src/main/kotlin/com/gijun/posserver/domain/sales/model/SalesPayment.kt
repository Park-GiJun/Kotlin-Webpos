package com.gijun.posserver.domain.sales.model

import com.gijun.posserver.domain.common.vo.Money
import java.time.LocalDateTime

data class SalesPayment(
    val id: Long?,
    val billId: Long,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val paymentMethodId: Long,
    val payAmt: Money,
    val saleType: Boolean,
    val paymentDate: LocalDateTime = LocalDateTime.now(),
    val changeAmt: Money = Money.ZERO,
    val isCompleted: Boolean = false
) {
    init {
        require(payAmt.isPositive()) { "Payment amount must be positive" }
        require(changeAmt >= Money.ZERO) { "Change amount cannot be negative" }
        require(hqId > 0) { "HQ ID must be positive" }
        require(storeId > 0) { "Store ID must be positive" }
        require(posId > 0) { "POS ID must be positive" }
        require(billId > 0) { "Bill ID must be positive" }
        require(paymentMethodId > 0) { "Payment method ID must be positive" }
    }

    fun isValidPayment(): Boolean = payAmt.isPositive() && paymentMethodId > 0

    fun isCashPayment(): Boolean = paymentMethodId == CASH_PAYMENT_METHOD_ID

    fun isCardPayment(): Boolean = paymentMethodId == CARD_PAYMENT_METHOD_ID

    fun complete(): SalesPayment = this.copy(isCompleted = true)

    fun withChange(changeAmount: Money): SalesPayment {
        require(changeAmount >= Money.ZERO) { "Change amount cannot be negative" }
        require(isCashPayment()) { "Change can only be applied to cash payments" }

        return this.copy(changeAmt = changeAmount)
    }

    fun getNetPaymentAmount(): Money = payAmt - changeAmt

    companion object {
        const val CASH_PAYMENT_METHOD_ID = 1L
        const val CARD_PAYMENT_METHOD_ID = 2L
        const val VOUCHER_PAYMENT_METHOD_ID = 3L

        fun createCashPayment(
            billId: Long,
            hqId: Long,
            storeId: Long,
            posId: Long,
            payAmt: Money,
            saleType: Boolean,
            changeAmt: Money = Money.ZERO
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
            payAmt: Money,
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
