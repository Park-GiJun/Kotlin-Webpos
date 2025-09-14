package com.gijun.posserver.domain.sales.model

import com.gijun.posserver.domain.common.*
import com.gijun.posserver.domain.common.vo.LineNo
import com.gijun.posserver.domain.common.vo.Money

data class Sales(
    val header: SalesHeader,
    val details: List<SalesDetail>,
    val payments: List<SalesPayment>
) {
    init {
        require(details.isNotEmpty()) { "Sales must have at least one detail item" }
        require(payments.isNotEmpty()) { "Sales must have at least one payment" }
        require(details.all { it.billId == header.id }) { "All details must belong to the same bill" }
    }

    fun getTotalSaleAmount(): Money = details.sumOf { it.saleAmt.amount }.let { Money.of(it) }

    fun getTotalPaymentAmount(): Money = payments.sumOf { it.payAmt.amount }.let { Money.of(it) }

    fun getTotalDiscountAmount(): Money = details.sumOf { it.getTotalDiscountAmount().amount }.let { Money.of(it) }

    fun isCompleteTransaction(): Boolean {
        val headerValid = header.isValidSale()
        val detailsValid = details.all { it.isValidLineItem() }
        val paymentsValid = payments.all { it.isValidPayment() }
        val amountsMatch = getTotalSaleAmount() == header.saleAmt &&
                          getTotalPaymentAmount() == header.payAmt

        return headerValid && detailsValid && paymentsValid && amountsMatch
    }

    fun addPayment(payment: SalesPayment): Sales {
        require(payment.payAmt.isPositive()) { "Payment amount must be positive" }

        return this.copy(payments = this.payments + payment)
    }

    fun applyDiscountToLine(lineNo: LineNo, discountAmount: Money): Sales {
        val updatedDetails = details.map { detail ->
            if (detail.lineNo == lineNo) {
                detail.applyDiscount(discountAmount)
            } else {
                detail
            }
        }

        val newTotalSaleAmt = updatedDetails.sumOf { it.saleAmt.amount }.let { Money.of(it) }
        val newTotalPayAmt = updatedDetails.sumOf { it.payAmt.amount }.let { Money.of(it) }
        val newTotalDiscountAmt = updatedDetails.sumOf { it.getTotalDiscountAmount().amount }.let { Money.of(it) }

        val updatedHeader = header.copy(
            saleAmt = newTotalSaleAmt,
            payAmt = newTotalPayAmt,
            dcAmt = newTotalDiscountAmt
        )

        return this.copy(
            header = updatedHeader,
            details = updatedDetails
        )
    }
}