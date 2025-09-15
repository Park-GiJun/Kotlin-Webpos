package com.gijun.posserver.domain.sales.model

import java.math.BigDecimal

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

    fun getTotalSaleAmount(): BigDecimal = details.sumOf { it.saleAmt }

    fun getTotalPaymentAmount(): BigDecimal = payments.sumOf { it.payAmt }

    fun getTotalDiscountAmount(): BigDecimal = details.sumOf { it.getTotalDiscountAmount() }

    fun isCompleteTransaction(): Boolean {
        val headerValid = header.isValidSale()
        val detailsValid = details.all { it.isValidLineItem() }
        val paymentsValid = payments.all { it.isValidPayment() }
        val amountsMatch = getTotalSaleAmount().compareTo(header.saleAmt) == 0 &&
                          getTotalPaymentAmount().compareTo(header.payAmt) == 0

        return headerValid && detailsValid && paymentsValid && amountsMatch
    }

    fun addPayment(payment: SalesPayment): Sales {
        require(payment.payAmt > BigDecimal.ZERO) { "Payment amount must be positive" }

        return this.copy(payments = this.payments + payment)
    }

    fun applyDiscountToLine(lineNo: Int, discountAmount: BigDecimal): Sales {
        val updatedDetails = details.map { detail ->
            if (detail.lineNo == lineNo) {
                detail.applyDiscount(discountAmount)
            } else {
                detail
            }
        }

        val newTotalSaleAmt = updatedDetails.sumOf { it.saleAmt }
        val newTotalPayAmt = updatedDetails.sumOf { it.payAmt }
        val newTotalDiscountAmt = updatedDetails.sumOf { it.getTotalDiscountAmount() }

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